FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build

COPY pom.xml .
COPY package.json package-lock.json* ./
COPY proto ./proto

RUN mvn dependency:go-offline -B -Dskip.frontend=true

COPY src ./src

# Render should package the checked-in static assets as-is. The frontend Maven
# plugin is skipped here so npm/Tailwind/PostCSS do not rewrite minified CSS.
RUN mvn clean package -DskipTests -Dskip.frontend=true

FROM eclipse-temurin:25-jre

ARG SCANNER_REPO=https://github.com/PDang176/mtg-ocr.git
ARG SCANNER_REF=main

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        build-essential \
        git \
        libglib2.0-0 \
        libgl1 \
        libgomp1 \
        libpq-dev \
        libpq5 \
        python3 \
        python3-dev \
        python3-pip \
        python3-venv \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/target/*.jar app.jar
COPY proto ./proto
COPY scripts/render-start.sh ./render-start.sh
COPY scanner-runtime ./scanner-runtime

RUN git clone --depth 1 --branch "${SCANNER_REF}" "${SCANNER_REPO}" mtg-scanner \
    && python3 -m venv mtg-scanner/.venv \
    && . mtg-scanner/.venv/bin/activate \
    && pip install --no-cache-dir --upgrade pip \
    && pip install --no-cache-dir -r mtg-scanner/requirements.txt \
    && pip install --no-cache-dir grpcio grpcio-tools protobuf \
    && cp scanner-runtime/grpc_server.py scanner-runtime/scanner_service.py mtg-scanner/ \
    && mkdir -p mtg-scanner/generated \
    && touch mtg-scanner/generated/__init__.py \
    && python -m grpc_tools.protoc \
        -Iproto \
        --python_out=mtg-scanner/generated \
        --grpc_python_out=mtg-scanner/generated \
        proto/card_scanner.proto \
    && rm -rf scanner-runtime \
    && chmod +x render-start.sh \
    && useradd --system --create-home --shell /usr/sbin/nologin spring \
    && chown -R spring:spring /app

USER spring

EXPOSE 8080

ENTRYPOINT ["./render-start.sh"]
