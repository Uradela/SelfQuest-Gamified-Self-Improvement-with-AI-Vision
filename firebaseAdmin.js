// firebaseAdmin.js
const admin = require('firebase-admin');
const serviceAccount = require('./capstone-selfquest-gamified-firebase-adminsdk-lu7ek-1c352da1a1.json');

// Initialize Firebase Admin SDK
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: 'imagegamified' // Replace with your storage bucket name
});

const db = admin.firestore();
const bucket = admin.storage().bucket();

module.exports = { db, bucket };
