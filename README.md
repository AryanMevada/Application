# DocToolkit

DocToolkit contains two deliverables:

- `android-file-converter/`: Android Studio Java app with a tools grid, SAF file picker, upload/cancel workflow, result preview/download/share, and Room-backed history of the last 10 outputs.
- `backend/`: Spring Boot Maven API for file conversion operations. Endpoints are `POST /convert`, `/compress`, `/merge`, `/split`, and `/ocr`.

## Backend notes

The backend uses PDFBox for PDF merge/split/compression and PDF generation, Apache POI for DOCX text extraction, and Tess4J/Tesseract for OCR. Configure Tesseract language data with `TESSDATA_PREFIX`; the default language string is `eng+hin+mar` for English, Hindi, and Marathi.

Run with:

```bash
cd backend
mvn spring-boot:run
```

## Android notes

The emulator backend URL is `http://10.0.2.2:8080/` in `UploadActivity`. Update it if testing on a physical device.

Open `android-file-converter/` in Android Studio and run the `app` module.

## Postman

Import `postman/DocToolkit.postman_collection.json` to exercise all backend endpoints.
