package com.saasdemo.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.service.PdfService;
import com.saasdemo.backend.service.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
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
  public final SubscriptionRepository subscriptionRepository;




//generate ticket of subscription
 @Operation(
    summary="REST API to print subsctiption pdf ticket  into APP ETAT CIVIL",
    description = "REST API to print subscription ticket  inside ETAT CIVIL App "
  )
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/subscriptionpdfprinting")
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
 @Operation(
    summary="REST API to print all subscription pdf ticket  into APP ETAT CIVIL",
    description = "REST API to print all subscription pdf ticket inside ETAT CIVIL App "
  )
@GetMapping("/subscriptionsallpdfprinting")
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



// List des souscriptions par tri dynamique
 @Operation(
    summary="REST API to print subscription pdf by sort  into APP ETAT CIVIL",
    description = "REST API to print subscription pdf by sort inside ETAT CIVIL App "
  )

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/subscriptionspdfprintingbsort")
public ResponseEntity<Page<Subscription>> listSubscriptionsDynamiques(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String fromDate // yyyy-MM-dd'T'HH:mm:ss
    ) {

        Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<Subscription> subscriptionsPage;

                if (fromDate != null && !fromDate.isEmpty()) {
              LocalDate startDate = LocalDate.parse(fromDate); // parse juste la date
              LocalDateTime startDateTime = startDate.atStartOfDay(); // converti en LocalDateTime
              subscriptionsPage = pdfService.getSubscriptionsByCommuneFromDate(
                      admin.getCommune(), startDateTime, page, size, sortBy, sortDir);
          } else {
              subscriptionsPage = pdfService.getSubscriptionsByCommune(
                      admin.getCommune(), page, size, sortBy, sortDir);
          }
        return ResponseEntity.ok(subscriptionsPage);
    }


//======================================================================================================
 @Operation(
    summary="REST API to print all birth certificate pdf into APP ETAT CIVIL",
    description = "REST API to print all birth certifcate  inside ETAT CIVIL App "
  )
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/allbirthcertifcatespdfprinting")
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