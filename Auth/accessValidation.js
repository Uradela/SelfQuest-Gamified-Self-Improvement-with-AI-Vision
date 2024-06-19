const jwt = require('jsonwebtoken');
const { db } = require('../firebaseAdmin');

const accessValidation = async (req, res, next) => {
  const { authorization } = req.headers;

  if (!authorization) {
    return res.status(401).json({
      status: false,
      message: "Unauthorized access",
    });
  }

  const token = authorization.split(" ")[1];
  const secret = process.env.SECRET_KEY;

  try {
    const decoded = jwt.verify(token, secret);
    req.user = decoded;

    const userSnapshot = await db.collection('users').doc(decoded.payload).get();
    if (!userSnapshot.exists) {
      return res.status(401).json({
        status: false,
        message: "Unauthorized access",
      });
    }

    next();
  } catch (error) {
    return res.status(401).json({
      status: false,
      message: "Unauthorized access",
    });
  }
};

module.exports = accessValidation;
