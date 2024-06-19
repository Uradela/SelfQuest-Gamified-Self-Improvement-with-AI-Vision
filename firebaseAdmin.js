const admin = require('firebase-admin');
const serviceAccount = require('./capstone-selfquest-gamified-firebase-adminsdk-lu7ek-1c352da1a1.json');

// Inisialisasi aplikasi Firebase Admin SDK dengan kredensial dari file JSON
const db = admin.firestore();
const bucket = admin.storage().bucket();

module.exports = { db, bucket };
