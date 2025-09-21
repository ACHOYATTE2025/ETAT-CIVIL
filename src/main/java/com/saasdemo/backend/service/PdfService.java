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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Birth;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.SubscriptionRepository;

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


// Pour String
private String safe(String value) {
    return value == null || value.trim().isEmpty() ? "Néant" : value;
}

// Pour n'importe quel objet
private String safe(Object obj) {
    return obj == null ? "Néant" : obj.toString();
}




public ByteArrayInputStream generateSubscriptionPdf() {
        
    try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
            throw new RuntimeException("Utilisateur non authentifié correctement.");
        }

        Optional<Subscription> subOpt = this.subscriptionRepository
                .findByUsersNameAndEndDateAfter(admin.getUsername(), LocalDateTime.now());

        if (subOpt.isEmpty()) {
            throw new RuntimeException("Aucune abonnement valide trouvé.");
        }

        Subscription sub = subOpt.get();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // --- Logo / Bandeau supérieur ---
        try {
            ImageData logoData = ImageDataFactory.create("src/main/resources/static/logo.png");
            Image logo = new Image(logoData).scaleToFit(100, 50).setFixedPosition(40, pdf.getDefaultPageSize().getHeight() - 80);
            document.add(logo);
        } catch (Exception ignore) {}

        Paragraph companyName = new Paragraph("SAAS ETAT-CIVIL")
                .setFont(bold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(companyName);

        document.add(new Paragraph("\n")); // espace

        // --- Titre du reçu ---
        Paragraph title = new Paragraph("REÇU D'ABONNEMENT")
                .setFont(bold)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        // --- Numéro du reçu et date ---
        Table infoTable = new Table(new float[]{150, 200}).useAllAvailableWidth();
        infoTable.addCell(new Cell().add(new Paragraph("Numéro du reçu:").setFont(bold)).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph(safe(sub.getNumero())).setFont(normal)).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph("Date:").setFont(bold)).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).setFont(normal)).setBorder(Border.NO_BORDER));
        document.add(infoTable);

        document.add(new Paragraph("\n"));

        // --- Informations client / abonnement ---
        Table table = new Table(new float[]{150, 250}).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Nom du client:").setFont(bold)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(sub.getUsersName()).setFont(normal)).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(new Paragraph("Organisation:").setFont(bold)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(sub.getCommune().getNameCommune()).setFont(normal)).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(new Paragraph("Montant payé:").setFont(bold)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(sub.getAmount() + " Fcfa").setFont(bold).setFontColor(ColorConstants.BLACK)).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(new Paragraph("Statut:").setFont(bold)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("ACTIF ✅").setFont(bold).setFontColor(ColorConstants.GREEN)).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(new Paragraph("Validité:").setFont(bold)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(sub.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).setFont(normal)).setBorder(Border.NO_BORDER));

        document.add(table);

        document.add(new Paragraph("\n"));

        // --- Ligne signature ---
        document.add(new Paragraph("Signature / Cachet de l’entreprise")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(bold)
                .setMarginTop(50));
        

        // --- Footer ---
        Paragraph footer = new Paragraph("Merci pour votre confiance ! Pour toute assistance, contactez-nous au +225 XXX XXX XXX")
                .setFont(normal)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);


        document.close();
        return new ByteArrayInputStream(out.toByteArray());

    } catch (Exception e) {
        throw new RuntimeException("Erreur génération PDF : " + e.getMessage(), e);
    }
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

    private void addTableRow(Table table, String string, String string2, PdfFont bold, Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addTableRow'");
}

    private void addTableRow(Table table, String string, String usersName, PdfFont bold) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addTableRow'");
}

    public ByteArrayInputStream generateAllSubscriptionPdf() throws Exception{

        ByteArrayInputStream pdfStreams = generateAllSubscriptionCommunePdf();

        return pdfStreams;

    }

// get all pdf subscriptions pdt dynamically
  public Page<Subscription> getSubscriptionsByCommune(Area commune, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return subscriptionRepository.findAllByCommune(commune, pageable);
    }

    public Page<Subscription> getSubscriptionsByCommuneFromDate(Area commune, LocalDateTime fromDate, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return subscriptionRepository.findAllByCommuneAndCreatedAfter(commune, fromDate, pageable);
    }


//===========================================================================================================
public ByteArrayInputStream generateBirthCertificatePdf(Birth birx) {
    try {
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(60, 50, 60, 50);

        PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas canvas = new PdfCanvas(page);

        // --- Bandeau tricolore ---
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

        // --- Armorie visible ---
        try {
            ImageData logoImg = ImageDataFactory.create("src/main/resources/static/armorie.png");
            Image logo = new Image(logoImg);
            logo.scaleToFit(80, 80);
            float xLogo = (pageSize.getWidth() - logo.getImageScaledWidth()) / 2;
            float yLogo = pageSize.getTop() - bandeauHeight - 100;
            logo.setFixedPosition(xLogo, yLogo);
            document.add(logo);
        } catch (Exception ignored) {}

        // --- Filigrane éléphant ---
        try {
            ImageData watermarkImg = ImageDataFactory.create("src/main/resources/static/elephant.png");
            Image watermark = new Image(watermarkImg);
            watermark.scaleToFit(250, 250);
            float x = (pageSize.getWidth() - watermark.getImageScaledWidth()) / 2;
            float y = (pageSize.getHeight() - watermark.getImageScaledHeight()) / 2;
            watermark.setFixedPosition(x, y);
            watermark.setRotationAngle(Math.toRadians(-20));
            watermark.setOpacity(0.05f);
            document.add(watermark);
        } catch (Exception ignored) {}

        // --- En-tête officiel ---
        document.add(new Paragraph("REPUBLIQUE DE CÔTE D’IVOIRE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(new DeviceRgb(255,127,0))); // orange
        document.add(new Paragraph("Union – Discipline – Travail")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(new DeviceRgb(0,128,0))); // vert
        document.add(new Paragraph("EXTRAIT DE NAISSANCE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(14)
                .setUnderline()
                .setMarginBottom(20));

        // --- Partie administrative et narratif en noir ---
        document.add(new Paragraph("Centre d’état civil : " + birth.getLieuDelivrance())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Acte n° " + birth.getNumeroExtrait()
                + " du registre de l’année " + birth.getDateNaissance().getYear())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(15));

        String dateNaissance = birth.getDateNaissance().getDayOfMonth() + " " +
                birth.getDateNaissance().getMonth() + " " + birth.getDateNaissance().getYear();
        document.add(new Paragraph(
                "Le " + dateNaissance + " est né(e) à " + birth.getLieuNaissance() +
                ", l’enfant " + birth.getNomComplet() + ".")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(20));

        // --- Informations père / mère ---
        document.add(new Paragraph("Informations sur le père").setFont(fontBold).setFontSize(12).setUnderline().setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Nom : " + getOrNeant(birth.getNomPere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Profession : " + getOrNeant(birth.getProfessionPere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Domicile : " + getOrNeant(birth.getDomicilePere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Nationalité : " + getOrNeant(birth.getNationalitePere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));

        document.add(new Paragraph("Informations sur la mère").setFont(fontBold).setFontSize(12).setUnderline().setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Nom : " + getOrNeant(birth.getNomMere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Profession : " + getOrNeant(birth.getProfessionMere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Domicile : " + getOrNeant(birth.getDomicileMere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Nationalité : " + getOrNeant(birth.getNationaliteMere())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));

        document.add(new Paragraph("Mentions").setFont(fontBold).setFontSize(12).setUnderline().setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Régime Matrimonial : " + getOrNeant(birth.getMarie())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Avec : " + getOrNeant(birth.getMarieAvec())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Décision DM : " + getOrNeant(birth.getNumeroDecisionDM())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Date dissolution : " + getOrNeant(birth.getDissolutionMariage())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));
        document.add(new Paragraph("Décès : " + getOrNeant(birth.getDeces())).setFont(fontNormal).setFontColor(ColorConstants.BLACK));

        // --- Pied de page ---
        document.add(new Paragraph("Certifié conforme au registre. Délivré le " + birth.getDateDelivrance()
                + " à " + birth.getLieuDelivrance())
                .setFont(fontNormal)
                .setFontSize(10));
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

// Utilitaire
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
            ByteArrayInputStream pdfStream = generateBirthCertificatePdf(abx);
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