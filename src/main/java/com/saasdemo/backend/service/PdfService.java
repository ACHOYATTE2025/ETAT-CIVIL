package com.saasdemo.backend.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Birth;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.SubscriptionRepository;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfService {


private Utilisateur admin=null;
private SubscriptionDTO dtox;
private String Validitex;

        

public final SubscriptionRepository subscriptionRepository;
public final BirthRepository birthRepository;

public PdfService (SubscriptionRepository subscriptionRepository, BirthRepository birthRepository){
        this.subscriptionRepository = subscriptionRepository;
        this.birthRepository = birthRepository;
}


//generate Valid subscription ticket 
 public ByteArrayInputStream generateSubscriptionPdf() {
        try {   
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
        throw new RuntimeException("Utilisateur non authentifié correctement.");}

                Optional<Subscription> subox = this.subscriptionRepository.findByUsersNameAndEndDateAfter(admin.getUsername(),LocalDateTime.now());

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Formatter la date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        DateTimeFormatter formatterx = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
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

            addTableRow(table, "Utilisateur:", subox.get().getUsersName(), bold);
            addTableRow(table, "Organisation:", subox.get().getCommune().getNameCommune(), bold);
            addTableRow(table, "Montant:", subox.get().getAmount() + " Fcfa", bold);
            addTableRow(table, "Statut:", "ACTIF ✅", bold, ColorConstants.GREEN);
            addTableRow(table, "Date d'émission:", subox.get().getCreated().format(formatter), bold);
            addTableRow(table, "Validité:",subox.get().getEndDate().format(formatterx), bold);

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







//liste de tous les reçus dans un seul fichier pdf
public ByteArrayInputStream generateAllSubscriptionCommunePdf() {

         Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
        throw new RuntimeException("Utilisateur non authentifié correctement.");}
        
        List<Subscription> abonnements = subscriptionRepository.findAllByCommune(admin.getCommune());

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // --- Titre global ---
            Paragraph title = new Paragraph("LISTE DES ABONNEMENTS - COMMUNE " + admin.getCommune().getNameCommune().toUpperCase())
                    .setFont(bold)
                    .setFontSize(18)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph("\n\n"));

            for (Subscription ab : abonnements) {
                // Sous-titre
                Paragraph subTitle = new Paragraph("CERTIFICAT D'ABONNEMENT")
                        .setFont(bold)
                        .setFontSize(14)
                        .setFontColor(ColorConstants.BLACK)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(subTitle);

                // Numéro reçu
                document.add(new Paragraph("N° " + UUID.randomUUID())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.GRAY));

                document.add(new Paragraph("\n"));

                // --- Tableau infos ---
                float[] columnWidths = {150, 250};
                Table table = new Table(columnWidths)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);

                addTableRow(table, "Utilisateur:", ab.getUsersName(), bold);
                addTableRow(table, "Organisation:", ab.getCommune().getNameCommune(), bold);
                addTableRow(table, "Montant:", ab.getAmount() + " Fcfa", bold);
                addTableRow(table, "Statut:", ab.getActive() ? "ACTIF ✅" : "INACTIF ❌", bold,
                        ab.getActive() ? ColorConstants.GREEN : ColorConstants.RED);
                addTableRow(table, "Date d'émission:", LocalDate.now().format(formatter), bold);
                addTableRow(table, "Validité:", ab.getEndDate().toString(), bold);

                document.add(table);

                document.add(new Paragraph("\n\n──────────────────────────────\n\n")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.GRAY));

                // Nouvelle page
                pdf.addNewPage();
            }

            document.close();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF de la commune", e);
        }
    }








    


    //generate all subscriptions of commune

    public ByteArrayInputStream generateAllSubscriptionPdf() throws Exception{

        ByteArrayInputStream pdfStreams = generateAllSubscriptionCommunePdf();

        return pdfStreams;

    }

// get all pdf subscriptions pdt dynamically
public Page<Subscription> getSubscriptionsByCommune(
        Area commune, int page, int size, String sortBy, String sortDir) {

    Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    // Récupérer depuis le repository et convertir en DTO
    return subscriptionRepository.findAllByCommune(commune, pageable);
           
}


//===========================================================================================================

public ByteArrayInputStream generateBirthCertificatepdf(Birth birx) {
    try {
        // --- Vérification utilisateur ---
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
            throw new RuntimeException("Utilisateur non authentifié correctement.");
        }

        Optional<Birth> birthOpt = birthRepository.findByNumeroExtraitAndCommune(
                birx.getNumeroExtrait(), admin.getCommune());
        if (birthOpt.isEmpty()) {
            throw new RuntimeException("Extrait de naissance introuvable.");
        }
        Birth birth = birthOpt.get();

        // --- Initialisation PDF ---
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(60, 50, 60, 50);

        PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        // --- Page et bandeau tricolore ---
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas canvas = new PdfCanvas(page);
        float bandeauHeight = 20;

        canvas.setFillColor(new DeviceRgb(255, 127, 0)); // Orange
        canvas.rectangle(pageSize.getLeft(), pageSize.getTop() - bandeauHeight, pageSize.getWidth()/3, bandeauHeight);
        canvas.fill();

        canvas.setFillColor(ColorConstants.WHITE);
        canvas.rectangle(pageSize.getLeft() + pageSize.getWidth()/3, pageSize.getTop() - bandeauHeight, pageSize.getWidth()/3, bandeauHeight);
        canvas.fill();

        canvas.setFillColor(new DeviceRgb(0, 128, 0)); // Vert
        canvas.rectangle(pageSize.getLeft() + 2*pageSize.getWidth()/3, pageSize.getTop() - bandeauHeight, pageSize.getWidth()/3, bandeauHeight);
        canvas.fill();

        // --- Filigrane centré et incliné ---
        try {
            ImageData watermarkImg = ImageDataFactory.create("src/main/resources/static/elephant.png");
            Image watermark = new Image(watermarkImg);

            // Taille et position
            watermark.scaleToFit(250, 250);
            float x = (pageSize.getWidth() - watermark.getImageScaledWidth()) / 2;
            float y = (pageSize.getHeight() - watermark.getImageScaledHeight()) / 2;
            watermark.setFixedPosition(x, y);

            // Rotation et opacité
            watermark.setRotationAngle(Math.toRadians(-20));
            PdfExtGState gs = new PdfExtGState().setFillOpacity(0.05f);
            watermark.getAccessibilityProperties(); // obligatoire pour certaines versions
            document.add(watermark);
        } catch (Exception ignore) {}

        // --- En-tête officiel ---
        document.add(new Paragraph("REPUBLIQUE DE CÔTE D’IVOIRE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Union – Discipline – Travail")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(new DeviceRgb(0, 128, 0))); // Vert
        document.add(new Paragraph("\nEXTRAIT DE NAISSANCE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(14)
                .setUnderline()
                .setMarginBottom(20));

        // --- Partie administrative centrée ---
        document.add(new Paragraph("Centre d’état civil : " + birth.getLieuDelivrance())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(new DeviceRgb(255, 127, 0))); // Orange
        document.add(new Paragraph("Acte n° " + birth.getNumeroExtrait()
                + " du registre de l’année " + birth.getDateNaissance().getYear())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(new DeviceRgb(0, 128, 0)) // Vert
                .setMarginBottom(15));

        // --- Corps narratif centré ---
        document.add(new Paragraph(
                "Le " + birth.getDateNaissance().getDayOfMonth() + " "
                        + birth.getDateNaissance().getMonth() + " "
                        + birth.getDateNaissance().getYear()
                        + " est né(e) à " + birth.getLieuNaissance()
                        + " l’enfant " + birth.getNomComplet()
        ).setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(20));

        // --- Sections encadrées Père / Mère avec titres colorés ---
        Table fatherTable = new Table(1).useAllAvailableWidth();
        fatherTable.setMarginBottom(10);
        fatherTable.addCell(new Cell().add(new Paragraph("Informations sur le père")
                .setFont(fontBold)
                .setFontColor(new DeviceRgb(255, 127, 0)))
                .setBackgroundColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.LEFT));
        fatherTable.addCell(new Cell().add(new Paragraph("Nom : " + getOrNeant(birth.getNomPere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        fatherTable.addCell(new Cell().add(new Paragraph("Profession : " + getOrNeant(birth.getProfessionPere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        fatherTable.addCell(new Cell().add(new Paragraph("Domicile : " + getOrNeant(birth.getDomicilePere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        fatherTable.addCell(new Cell().add(new Paragraph("Nationalité : " + getOrNeant(birth.getNationalitePere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        document.add(fatherTable);

        Table motherTable = new Table(1).useAllAvailableWidth();
        motherTable.setMarginBottom(10);
        motherTable.addCell(new Cell().add(new Paragraph("Informations sur la mère")
                .setFont(fontBold)
                .setFontColor(new DeviceRgb(0, 128, 0)))
                .setBackgroundColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.LEFT));
        motherTable.addCell(new Cell().add(new Paragraph("Nom : " + getOrNeant(birth.getNomMere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        motherTable.addCell(new Cell().add(new Paragraph("Profession : " + getOrNeant(birth.getProfessionMere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        motherTable.addCell(new Cell().add(new Paragraph("Domicile : " + getOrNeant(birth.getDomicileMere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        motherTable.addCell(new Cell().add(new Paragraph("Nationalité : " + getOrNeant(birth.getNationaliteMere()))
                .setFont(fontBold).setFontColor(ColorConstants.BLACK)));
        document.add(motherTable);

        // --- Mentions marginales ---
        document.add(new Paragraph("Mentions")
                .setFont(fontBold)
                .setFontSize(12)
                .setUnderline()
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(5));
        document.add(new Paragraph("Regime Matrimoniale: " + getOrNeant(birth.getMarie())).setFont(fontBold));
        document.add(new Paragraph("Avec : " + getOrNeant(birth.getMarieAvec())).setFont(fontBold));
        document.add(new Paragraph("Décision DM : " + getOrNeant(birth.getNumeroDecisionDM())).setFont(fontBold));
        document.add(new Paragraph("Date dissolution : " + getOrNeant(birth.getDissolutionMariage())).setFont(fontBold));
        document.add(new Paragraph("Décès : " + getOrNeant(birth.getDeces())).setFont(fontBold).setMarginBottom(15));

        // --- Pied de page ---
        document.add(new Paragraph("Certifié conforme au registre."
                + "Délivré le " + birth.getDateDelivrance()
                + " à " + birth.getLieuDelivrance())
                .setFont(fontNormal)
                .setFontSize(10)
                .setMarginTop(20));
        document.add(new Paragraph("L’Officier de l’État Civil")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(fontBold)
                .setFontSize(11)
                .setMarginTop(30));

        document.close();
        return new ByteArrayInputStream(out.toByteArray());

    } catch (Exception e) {
        throw new RuntimeException("Erreur génération PDF : " + e.getMessage(), e);
    }
}

// Utilitaire : si vide/null → "Néant"
private String getOrNeant(Object value) {
    if (value == null) return "Néant";
    String str = value.toString().trim();
    return str.isEmpty() ? "Néant" : str;
}


// Publier la liste des extraits de naissance en PDF
public List<ByteArrayInputStream> generateAllBirthCertificatePdf() {

    // --- Vérification utilisateur ---
    Utilisateur principal = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("Utilisateur : " + principal);

    if (principal == null) {
        throw new RuntimeException("Utilisateur non authentifié correctement.");
    }

    try {
        List<Birth> extraits = this.birthRepository.findAll();
        log.info("Liste des actes de naissance : " + extraits);

        // Initialisation de la liste de PDFs
        List<ByteArrayInputStream> pdfStreams = new ArrayList<>();

        for (Birth abx : extraits) {
            ByteArrayInputStream pdfStream = generateBirthCertificatepdf(abx);
            pdfStreams.add(pdfStream);
        }

        if (pdfStreams.isEmpty()) {
            throw new RuntimeException("Aucun extrait trouvé.");
        }

        return pdfStreams;

    } catch (Exception e) {
        throw new RuntimeException("Erreur lors de la génération des extraits : " + e.getMessage(), e);
    }
}




}