package com.saasdemo.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.service.PdfService;
import com.saasdemo.backend.service.SubscriptionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor


@Tag(
  name = "GENERATE PDF for ETAT CIVIL",
  description="GENERATE PDF  in  ETAT CIVIL APP SHOW ALL DETAILS"
)
public class PdfGenerateController {

  public final PdfService pdfService;
  public final SubscriptionService subscriptionService;




//generate ticket of subscription
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/subscriptionpdf")
public ResponseEntity<byte[]> getSubscriptionPdf() throws IOException {
    ByteArrayInputStream pdfStream = this.pdfService.generateSubscriptionPdf();

    byte[] pdfBytes = pdfStream.readAllBytes();

    return ResponseEntity.ok()
            //.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=subscription.pdf") voir le fichier dans le navigateur
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subscription.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}


//generate  list of Subscription
@GetMapping("/subscriptionsallpdf")
@PreAuthorize("hasRole('SUPERADMIN')")
public ResponseEntity<byte[]> getAllSubscriptionPdf() throws Exception {
    ByteArrayInputStream pdfStream = this.pdfService.generateAllSubscriptionCommunePdf();

    byte[] pdfBytes = pdfStream.readAllBytes();

    return ResponseEntity.ok()
            //.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=subscription.pdf") voir le fichier dans le navigateur
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subscription.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}



// List des souscriptions par tri dynzmique

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/subscriptionslistbytri")
public ResponseEntity<Page<com.saasdemo.backend.entity.Subscription>> listSubscriptionsDynamiques(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "startDate") String sortBy,   // tri par date de souscription par défaut
        @RequestParam(defaultValue = "asc") String sortDir) {

    // Récupérer l’utilisateur connecté
    Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Appeler le service pour récupérer les abonnements de la commune de l’admin
    Page<com.saasdemo.backend.entity.Subscription> subscriptionsPage = pdfService.getSubscriptionsByCommune(
            admin.getCommune(), page, size, sortBy, sortDir);

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(subscriptionsPage);
            
}


//======================================================================================================

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/allbirthcertifcatespdf")
public ResponseEntity<byte[]> getAllBirthCertificatesPdf() throws IOException {
    List<ByteArrayInputStream> pdfStreams = this.pdfService.generateAllBirthCertificatePdf();

    ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();

    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(mergedOutput));
    PdfMerger merger = new PdfMerger(pdfDoc);

    for (ByteArrayInputStream bais : pdfStreams) {
        PdfDocument tempDoc = new PdfDocument(new PdfReader(bais));
        merger.merge(tempDoc, 1, tempDoc.getNumberOfPages());
        tempDoc.close();
    }

    pdfDoc.close();

    byte[] pdfBytes = mergedOutput.toByteArray();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_birth_certificates.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}


    
}