package com.saasdemo.backend.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {


 public ByteArrayInputStream generateSubscriptionPdf(String nomUtilisateur, String organisation, double montant, String validite) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Formatter la date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateEmission = LocalDate.now().format(formatter);

            // Font en gras
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // --- Titre ---
            Paragraph title = new Paragraph("CERTIFICAT D'ABONNEMENT")
                    .setFont(bold)
                    .setFontSize(20)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph("──────────────────────────────")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            // Numéro du reçu
            document.add(new Paragraph("N° " + UUID.randomUUID().toString())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY));

            document.add(new Paragraph("\n")); // espace

            // --- Tableau centré ---
            float[] columnWidths = {150, 250};
            Table table = new Table(columnWidths)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER); // <-- tableau centré
            table.setTextAlignment(TextAlignment.LEFT);
            table.setVerticalAlignment(VerticalAlignment.MIDDLE);

            addTableRow(table, "Utilisateur:", nomUtilisateur, bold);
            addTableRow(table, "Organisation:", organisation, bold);
            addTableRow(table, "Montant:", montant + " Fcfa", bold);
            addTableRow(table, "Statut:", "ACTIF ✅", bold, ColorConstants.GREEN);
            addTableRow(table, "Date d'émission:", dateEmission, bold);
            addTableRow(table, "Validité:", validite, bold);

            document.add(table);

            document.add(new Paragraph("\n")); // espace

            // Footer
            Paragraph footer = new Paragraph("Merci pour votre confiance !\nPour toute assistance, contactez-nous.")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

            document.close();

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException | java.io.IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    // Méthode utilitaire pour ajouter une ligne au tableau
    private void addTableRow(Table table, String key, String value, PdfFont bold) {
        addTableRow(table, key, value, bold, null);
    }

    private void addTableRow(Table table, String key, String value, PdfFont bold, com.itextpdf.kernel.colors.Color color) {
        Cell keyCell = new Cell().add(new Paragraph(key).setFont(bold));
        keyCell.setPadding(5);
        table.addCell(keyCell);

        Paragraph valueParagraph = new Paragraph(value);
        if (color != null) {
            valueParagraph.setFontColor(color);
        }
        Cell valueCell = new Cell().add(valueParagraph);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

}