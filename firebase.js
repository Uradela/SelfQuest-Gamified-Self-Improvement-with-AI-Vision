const admin = require("firebase-admin");

const serviceAccount = require("D:/UNAIR/Semester 6/Stupen/Mengcoding/test 2/SelfQuest-Gamified-Self-Improvement-with-AI-Vision/capstone-selfquest-gamified-firebase-adminsdk-lu7ek-1c352da1a1.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: 'imagegamified' // Ganti dengan nama bucket penyimpanan Anda
});

const db = admin.firestore(); 
const bucket = admin.storage().bucket(); // Inisialisasi bucket Firebase Storage

module.exports = { db, bucket }; // Export db dan bucket
