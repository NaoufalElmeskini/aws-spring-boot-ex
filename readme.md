## Introduction
Exemple de projet java integré avec Aws Lambda et S3.

---
### Fonctionnalité 
Ce projet permet le scénario suivant : 
- transcrire une fichier audio via une api [speech-to-text](https://fr.wikipedia.org/wiki/Reconnaissance_automatique_de_la_parole) comme [Whisper](https://en.wikipedia.org/wiki/Whisper_(speech_recognition_system)) 

---
### Deploiement
ce projet inclut des [scripts](scripts) permettant de 
- déployer automatiquement une [lambda AWS](https://en.wikipedia.org/wiki/AWS_Lambda)
- appeler un [AccessPoint de transformation](https://aws.amazon.com/s3/features/object-lambda/) 

---
### Condition et prerequis :
- AWS : l'Object-Lambda doit être correctement cablé avec le nom de la lambda correspondant à ce projet 
- AWS : variable d'environnement OPENAI_API_KEY : clé de l'api Whisper
- formats de fichier supporté : .wav 