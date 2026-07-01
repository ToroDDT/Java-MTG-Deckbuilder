import cv2
import numpy as np
from PIL import Image

from card_classifier import CardClassifier
from config import Config
from database.card_loader import CardLoader
from processors.image_processor import ImageProcessor
from processors.rapidocr_processor import RapidOCRProcessor


class ScannerService:
    def __init__(self):
        self.processor = ImageProcessor()
        self.ocr = RapidOCRProcessor()
        card_loader = CardLoader(Config())
        self.classifier = CardClassifier(card_loader.load_all_names())

    def scan_image_bytes(self, image_bytes):
        image_array = np.frombuffer(image_bytes, dtype=np.uint8)
        frame = cv2.imdecode(image_array, cv2.IMREAD_COLOR)

        if frame is None:
            return {"success": False, "card_name": "", "message": "Image could not be decoded."}

        return self.scan_frame(frame)

    def scan_frame(self, frame):
        _, cards = self.processor.edge_detection(frame)

        if not cards:
            return {"success": False, "card_name": "", "message": "No card detected in image."}

        best_name = ""
        for card_image, _, _ in cards:
            card_name = self._scan_card_image(card_image)
            if card_name:
                best_name = card_name
                break

        if not best_name:
            return {"success": False, "card_name": "", "message": "Unable to confidently identify the card."}

        return {"success": True, "card_name": best_name, "message": ""}

    def _scan_card_image(self, card_image):
        pillow_img = Image.fromarray(cv2.cvtColor(card_image, cv2.COLOR_BGR2RGB))
        cropped_name = self.processor.crop_image(pillow_img)
        raw, clean = self.ocr.extract_text(cropped_name)

        if not raw:
            return ""

        best_match, _, confident = self.classifier.classify(clean)
        if not confident:
            return ""

        return best_match
