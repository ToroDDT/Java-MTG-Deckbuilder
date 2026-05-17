package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.scanner.proto.CardScannerServiceGrpc;
import com.example.mtg_deckbuilder.scanner.proto.ScanCardImageRequest;
import com.example.mtg_deckbuilder.scanner.proto.ScanCardImageResponse;
import com.example.mtg_deckbuilder.exceptions.CardScanFailedException;
import com.example.mtg_deckbuilder.service.api.CardScannerClient;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class GrpcCardScannerClient implements CardScannerClient {

    private final CardScannerServiceGrpc.CardScannerServiceBlockingStub scannerStub;

    public GrpcCardScannerClient(ManagedChannel scannerChannel) {
        this.scannerStub = CardScannerServiceGrpc.newBlockingStub(scannerChannel);
    }

    @Override
    public String scanCard(byte[] imageBytes, String filename, String contentType) {
        ScanCardImageRequest request = ScanCardImageRequest.newBuilder()
                .setImageData(ByteString.copyFrom(imageBytes))
                .setFilename(filename == null ? "" : filename)
                .setContentType(contentType == null ? "" : contentType)
                .build();

        final ScanCardImageResponse response;
        try {
            response = scannerStub.scanCardImage(request);
        } catch (StatusRuntimeException ex) {
            throw new CardScanFailedException("Scanner service is unavailable.", ex);
        }

        if (!response.getSuccess()) {
            throw new CardScanFailedException(response.getMessage().isBlank()
                    ? "Scanner could not identify a card."
                    : response.getMessage());
        }

        return response.getCardName();
    }
}
