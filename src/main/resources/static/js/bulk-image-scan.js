(function () {
    function ready(callback) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", callback);
            return;
        }
        callback();
    }

    ready(function () {
        const form = document.getElementById("scanCardForm");
        const input = document.getElementById("scan-file");
        const reviewButton = document.getElementById("reviewBulkScanBtn");
        const modal = document.getElementById("bulkImageModal");
        const closeButton = document.getElementById("closeBulkImageModalBtn");
        const clearButton = document.getElementById("clearBulkImagesBtn");
        const addMoreButton = document.getElementById("addMoreBulkImagesBtn");
        const scanButton = document.getElementById("scanBulkImagesBtn");
        const grid = document.getElementById("bulkImageGrid");
        const count = document.getElementById("bulkImageCount");
        const status = document.getElementById("bulkImageStatus");
        const results = document.getElementById("card-query-results");

        if (!form || !input || !reviewButton || !modal || !scanButton || !grid) {
            return;
        }

        let objectUrls = [];
        let selectedFiles = [];

        function imageFilesFromInput() {
            return Array.from(input.files || []).filter(function (file) {
                return file.type && file.type.indexOf("image/") === 0;
            });
        }

        function fileKey(file) {
            return [file.name, file.size, file.lastModified].join(":");
        }

        function syncInputFiles() {
            const dataTransfer = new DataTransfer();
            selectedFiles.forEach(function (file) {
                dataTransfer.items.add(file);
            });
            input.files = dataTransfer.files;
        }

        function addSelectedInputFiles() {
            const existingKeys = new Set(selectedFiles.map(fileKey));
            imageFilesFromInput().forEach(function (file) {
                const key = fileKey(file);
                if (!existingKeys.has(key)) {
                    selectedFiles.push(file);
                    existingKeys.add(key);
                }
            });
            syncInputFiles();
        }

        function clearObjectUrls() {
            objectUrls.forEach(function (url) {
                URL.revokeObjectURL(url);
            });
            objectUrls = [];
        }

        function setStatus(message, tone) {
            if (!status) {
                return;
            }
            status.textContent = message || "";
            status.dataset.tone = tone || "";
        }

        function renderSelectedImages() {
            clearObjectUrls();
            grid.innerHTML = "";

            if (count) {
                count.textContent = selectedFiles.length === 1 ? "1 image selected." : selectedFiles.length + " images selected.";
            }

            selectedFiles.forEach(function (file) {
                const url = URL.createObjectURL(file);
                objectUrls.push(url);

                const item = document.createElement("figure");
                item.className = "bulk-image-item";

                const image = document.createElement("img");
                image.src = url;
                image.alt = file.name;

                const caption = document.createElement("figcaption");
                caption.textContent = file.name;
                caption.title = file.name;

                item.append(image, caption);
                grid.appendChild(item);
            });

            scanButton.disabled = selectedFiles.length === 0;
            setStatus(selectedFiles.length === 0 ? "Choose one or more image files first." : "", selectedFiles.length === 0 ? "error" : "");
        }

        function openModal() {
            renderSelectedImages();
            modal.classList.add("is-open");
            modal.setAttribute("aria-hidden", "false");
            document.body.style.overflow = "hidden";
        }

        function closeModal() {
            modal.classList.remove("is-open");
            modal.setAttribute("aria-hidden", "true");
            document.body.style.overflow = "";
        }

        function clearSelection() {
            selectedFiles = [];
            input.value = "";
            renderSelectedImages();
            closeModal();
        }

        function csrfHeaders() {
            const tokenMeta = document.querySelector('meta[name="_csrf"]');
            const headerMeta = document.querySelector('meta[name="_csrf_header"]');
            const token = tokenMeta && tokenMeta.getAttribute("content");
            const header = headerMeta && headerMeta.getAttribute("content");
            const headers = { "HX-Request": "true" };

            if (token && header) {
                headers[header] = token;
            }
            return headers;
        }

        async function scanFile(file) {
            const formData = new FormData();
            formData.append("file", file, file.name);

            const response = await fetch(form.getAttribute("action") || "/personal-library/scan", {
                method: "POST",
                body: formData,
                headers: csrfHeaders()
            });
            const html = await response.text();

            if (!response.ok) {
                throw new Error(html || "Unable to scan " + file.name + ".");
            }
            return html;
        }

        async function scanSelectedImages() {
            const files = selectedFiles.slice();
            if (files.length === 0) {
                setStatus("Choose one or more image files first.", "error");
                return;
            }

            scanButton.disabled = true;
            reviewButton.disabled = true;

            let completed = 0;
            let lastHtml = "";

            for (let index = 0; index < files.length; index += 1) {
                const file = files[index];
                setStatus("Scanning " + (index + 1) + " of " + files.length + ": " + file.name, "");

                try {
                    lastHtml = await scanFile(file);
                    completed += 1;
                } catch (error) {
                    setStatus(error.message || "Unable to scan " + file.name + ".", "error");
                    scanButton.disabled = false;
                    reviewButton.disabled = false;
                    return;
                }
            }

            if (results && lastHtml) {
                results.innerHTML = lastHtml;
            }
            if (window.htmx) {
                htmx.trigger(document.body, "refreshLibrary");
            }

            selectedFiles = [];
            input.value = "";
            renderSelectedImages();
            setStatus(completed + " " + (completed === 1 ? "image" : "images") + " scanned and added.", "success");
            scanButton.disabled = false;
            reviewButton.disabled = false;
        }

        form.addEventListener("submit", function (event) {
            event.preventDefault();
            openModal();
        });
        input.addEventListener("change", function () {
            addSelectedInputFiles();
            openModal();
        });
        reviewButton.addEventListener("click", openModal);
        scanButton.addEventListener("click", scanSelectedImages);

        if (closeButton) {
            closeButton.addEventListener("click", closeModal);
        }
        if (clearButton) {
            clearButton.addEventListener("click", clearSelection);
        }
        if (addMoreButton) {
            addMoreButton.addEventListener("click", function () {
                input.value = "";
                input.click();
            });
        }

        modal.addEventListener("click", function (event) {
            if (event.target === modal) {
                closeModal();
            }
        });
        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape" && modal.classList.contains("is-open")) {
                closeModal();
            }
        });
        window.addEventListener("beforeunload", clearObjectUrls);
    });
}());
