const express = require('express');
const bcrypt = require('bcryptjs');
const { db, bucket } = require('../firebaseAdmin'); // Correct path to firebaseAdmin.js
const upload = require('./uploadMiddleware');

const router = express.Router();

const generateRandomID = () => {
  const min = 10000;
  const max = 99999;
  return Math.floor(Math.random() * (max - min + 1)) + min;
};

const createCustomID = () => {
  const prefix = "120";
  const randomNumber = generateRandomID();
  return `${prefix}${randomNumber}`;
};

router.post('/signup', async (req, res) => {
  const { email, username, password, confirmPassword } = req.body;

  if (!email || !username || !password || !confirmPassword) {
    return res.status(400).json({ error: 'Please enter all data correctly' });
  }

  if (password !== confirmPassword) {
    return res.status(400).send('Passwords do not match');
  }

  try {
    const usersRef = db.collection('users');
    const snapshotEmail = await usersRef.where('email', '==', email).get();
    const snapshotUsername = await usersRef.where('username', '==', username).get();

    if (!snapshotEmail.empty) {
      return res.status(400).send('Email already in use');
    }

    if (!snapshotUsername.empty) {
      return res.status(400).send('Username already in use');
    }

    const userID = createCustomID().toString();
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = {
      id: userID,
      username,
      email,
      password: hashedPassword,
      fullName: username,
      gender: "",
      dateOfBirth: "",
      address: "",
      cities: "",
      weight: "",
      height: "",
      age: ""
    };

    const newDoc = await usersRef.add(newUser);
    const uid = newDoc.id;
    const userDocRef = db.collection('users').doc(uid);

    await userDocRef.set({ ...newUser, uid });
    res.status(200).send('Account has been created');
  } catch (error) {
    res.status(500).send('Error creating user: ' + error.message);
  }
});

router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;

    if (!username || !password) {
      return res.status(400).json({ error: 'Please enter all data correctly' });
    }

    const userSnapshot = await db.collection("users").where("username", "==", username).get();

    if (userSnapshot.empty) {
      return res.status(404).json({ error: 'Data is not found' });
    }

    const userData = userSnapshot.docs[0].data();
    const hashedPassword = userData.password;

    const passwordMatch = await bcrypt.compare(password, hashedPassword);
    if (!passwordMatch) {
      return res.status(401).json({ error: 'Wrong password' });
    }

    return res.status(200).json({ message: 'Login Success', user: userData });
  } catch (error) {
    console.error('Error:', error);
    return res.status(500).json({ error: 'An error occurred during the login process', details: error.message });
  }
});

router.post('/edit-profile', async (req, res) => {
  try {
    const userId = req.body.uid;
    const userData = req.body;

    const requiredFields = ['email', 'phone', 'fullName', 'gender', 'dateOfBirth', 'address', 'cities', 'weight', 'height', 'age'];
    for (const field of requiredFields) {
      if (!userData[field]) {
        return res.status(400).send(`Field ${field} is required`);
      }
    }

    const userRef = db.collection('users').doc(userId);
    const userDoc = await userRef.get();
    if (!userDoc.exists) {
      return res.status(404).send('User not found');
    }

    await userRef.set(userData, { merge: true });
    res.status(200).send('Profile updated successfully');
  } catch (error) {
    res.status(500).send('Error updating profile: ' + error.message);
  }
});

router.post('/reset-password', async (req, res) => {
  const { uidLocal, currentPassword, newPassword, confirmNewPassword } = req.body;

  if (!currentPassword || !newPassword || !confirmNewPassword) {
    return res.status(400).json({ error: 'Please enter all data correctly' });
  }

  try {
    if (newPassword !== confirmNewPassword) {
      return res.status(400).send('New passwords do not match');
    }

    const userDocRef = db.collection("users").doc(uidLocal);
    const userDocSnapshot = await userDocRef.get();

    if (!userDocSnapshot.exists) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = userDocSnapshot.data();
    const userPassword = user.password;
    const isPasswordValid = await bcrypt.compare(currentPassword, userPassword);

    if (!isPasswordValid) {
      return res.status(400).send('Current password is incorrect');
    }

    const isNewPasswordSameAsOld = await bcrypt.compare(newPassword, userPassword);

    if (isNewPasswordSameAsOld) {
      return res.status(400).send('New password must be different from old password');
    }

    const hashedNewPassword = await bcrypt.hash(newPassword, 10);

    await userDocRef.update({ password: hashedNewPassword });
    res.status(200).send('Password changed successfully');
  } catch (error) {
    res.status(500).send('Error changing password: ' + error.message);
  }
});

router.post('/upload', upload.single('file'), async (req, res) => {
  if (!req.file) {
    return res.status(400).send('No file uploaded.');
  }

  try {
    const blob = bucket.file(req.file.originalname);
    const blobStream = blob.createWriteStream({
      metadata: {
        contentType: req.file.mimetype,
      },
    });

    blobStream.on('error', (err) => {
      res.status(500).send({ error: 'Error uploading file: ' + err.message });
    });

    blobStream.on('finish', async () => {
      const publicUrl = `https://storage.googleapis.com/${process.env.BUCKET_NAME}/${blob.name}`;
      res.status(200).send({ message: 'File uploaded successfully', url: publicUrl });
    });

    blobStream.end(req.file.buffer);
  } catch (error) {
    if (error.code === 403 && error.message.includes('The billing account for the owning project is disabled')) {
      res.status(403).send('Billing account is disabled. Please enable the billing account for this project.');
    } else {
      res.status(500).send('Error uploading file: ' + error.message);
    }
  }
});


module.exports = router;
