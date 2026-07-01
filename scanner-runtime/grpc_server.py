import argparse
import os
import sys
from concurrent import futures
from pathlib import Path

import grpc

GENERATED_DIR = Path(__file__).resolve().parent / "generated"
if str(GENERATED_DIR) not in sys.path:
    sys.path.append(str(GENERATED_DIR))

from generated import card_scanner_pb2
from generated import card_scanner_pb2_grpc
from scanner_service import ScannerService


DEFAULT_PORT = 50051


class CardScannerGrpcService(card_scanner_pb2_grpc.CardScannerServiceServicer):
    def __init__(self, scanner_service):
        self.scanner_service = scanner_service

    def ScanCardImage(self, request, context):
        result = self.scanner_service.scan_image_bytes(request.image_data)
        return card_scanner_pb2.ScanCardImageResponse(
            success=result["success"],
            card_name=result["card_name"],
            message=result["message"],
        )


def serve(port):
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=4))
    card_scanner_pb2_grpc.add_CardScannerServiceServicer_to_server(
        CardScannerGrpcService(ScannerService()),
        server,
    )
    server.add_insecure_port(f"[::]:{port}")
    server.start()
    print(f"Card scanner gRPC listening on {port}", flush=True)
    server.wait_for_termination()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", type=int, default=int(os.getenv("SCANNER_PORT", DEFAULT_PORT)))
    args = parser.parse_args()
    serve(args.port)
