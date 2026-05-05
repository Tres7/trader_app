package com.traderapp.modules.plan.infrastructure.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import com.traderapp.modules.plan.application.ports.output.PlanExportPort;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.entities.TradingPlanCustomField;
import com.traderapp.modules.plan.domain.entities.TradingPlanSection;

@Component
public class PdfBoxPlanExportAdapter implements PlanExportPort {

    private static final float MARGIN = 50f;
    private static final float LINE_HEIGHT = 20f;
    private static final float TITLE_SIZE = 18f;
    private static final float SECTION_TITLE_SIZE = 12f;
    private static final float CONTENT_SIZE = 11f;

    @Override
    public byte[] exportAsPdf(TradingPlan plan) {
        try (PDDocument document = new PDDocument()) {
            PdfWriter writer = new PdfWriter(document);

            writer.addTitle("Mon Plan de Trading");
            writer.addSeparator();

            List<TradingPlanSection> filledSections = plan.getSections().stream()
                .filter(s -> s.getContent() != null && !s.getContent().isBlank())
                .collect(java.util.stream.Collectors.toList());

            List<TradingPlanCustomField> filledFields = plan.getCustomFields().stream()
                .filter(f -> f.getFieldValue() != null && !f.getFieldValue().isBlank())
                .collect(java.util.stream.Collectors.toList());

            if (!filledSections.isEmpty()) {
                writer.addHeading("Champs standards");
                for (TradingPlanSection section : filledSections) {
                    writer.addField(section.getSectionKey().getLabel(), section.getContent());
                }
            }

            if (!filledFields.isEmpty()) {
                if (!filledSections.isEmpty()) writer.addSeparator();
                writer.addHeading("Champs personnalisés");
                for (TradingPlanCustomField field : filledFields) {
                    writer.addField(field.getFieldName(), field.getFieldValue());
                }
            }

            writer.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF", e);
        }
    }


    private static class PdfWriter {
        private final PDDocument document;
        private PDPage currentPage;
        private PDPageContentStream stream;
        private float y;

        private final PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private final PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDType1Font fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        void addTitle(String text) throws IOException {
            checkSpace(40f);
            stream.setFont(fontBold, TITLE_SIZE);
            stream.beginText();
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(text);
            stream.endText();
            y -= 30f;
        }

        void addHeading(String text) throws IOException {
            checkSpace(LINE_HEIGHT * 2);
            y -= 10f;
            stream.setFont(fontBold, SECTION_TITLE_SIZE);
            stream.beginText();
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(text.toUpperCase());
            stream.endText();
            y -= LINE_HEIGHT;
        }

        void addField(String label, String value) throws IOException {
            checkSpace(LINE_HEIGHT * 2);
            stream.setFont(fontBold, CONTENT_SIZE);
            stream.beginText();
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(sanitize(label) + " :");
            stream.endText();
            y -= LINE_HEIGHT;

            String[] lines = wrap(value, 85);
            checkSpace(LINE_HEIGHT * lines.length);
            stream.setFont(fontRegular, CONTENT_SIZE);
            for (String line : lines) {
                stream.beginText();
                stream.newLineAtOffset(MARGIN + 15f, y);
                stream.showText(sanitize(line));
                stream.endText();
                y -= LINE_HEIGHT;
            }
        }

        void addSeparator() throws IOException {
            checkSpace(15f);
            y -= 5f;
            stream.setLineWidth(0.5f);
            stream.moveTo(MARGIN, y);
            stream.lineTo(PDRectangle.A4.getWidth() - MARGIN, y);
            stream.stroke();
            y -= 10f;
        }

        void close() throws IOException {
            stream.close();
        }

        private void newPage() throws IOException {
            if (stream != null) stream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            stream = new PDPageContentStream(document, currentPage);
            y = PDRectangle.A4.getHeight() - MARGIN;
        }

        private void checkSpace(float needed) throws IOException {
            if (y - needed < MARGIN) newPage();
        }

        private String[] wrap(String text, int maxChars) {
            if (text.length() <= maxChars) return new String[]{text};
            java.util.List<String> lines = new java.util.ArrayList<>();
            while (text.length() > maxChars) {
                int cut = text.lastIndexOf(' ', maxChars);
                if (cut == -1) cut = maxChars;
                lines.add(text.substring(0, cut));
                text = text.substring(cut).stripLeading();
            }
            if (!text.isEmpty()) lines.add(text);
            return lines.toArray(new String[0]);
        }

        private String sanitize(String text) {
            return text == null ? "" : text.replaceAll("[\\p{Cc}&&[^\t\n\r]]", "");
        }
    }
}
