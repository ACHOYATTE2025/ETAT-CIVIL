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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(
  name = "GENERATE PDF for ETAT CIVIL",
  description="GENERATE PDF  in  ETAT CIVIL APP SHOW ALL DETAILS"
)
public class PdfGenerateController {

  public final PdfService pdfService;
  public final SubscriptionService subscriptionService;
  public final SubscriptionRepository subscriptionRepository;

  // Generate a subscription ticket PDF
  @Operation(
      summary="REST API to print subscription PDF ticket into ETAT CIVIL APP",
      description = "REST API to print subscription ticket inside ETAT CIVIL App "
  )
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/subscriptionpdfprinting")
  public ResponseEntity<byte[]> getSubscriptionPdf() throws IOException {
      log.info("➡️ Request to generate subscription PDF ticket");
      ByteArrayInputStream pdfStream = this.pdfService.generateSubscriptionPdf();
      byte[] pdfBytes = pdfStream.readAllBytes();
      log.info("✅ Subscription PDF generated, size: {} bytes", pdfBytes.length);

      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subscription.pdf")
              .contentType(MediaType.APPLICATION_PDF)
              .body(pdfBytes);
  }

  // Generate a PDF with the list of all subscriptions
  @Operation(
      summary="REST API to print all subscription PDF tickets into ETAT CIVIL APP",
      description = "REST API to print all subscription PDF tickets inside ETAT CIVIL App "
  )
  @GetMapping("/subscriptionsallpdfprinting")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<byte[]> getAllSubscriptionPdf() throws Exception {
      log.info("➡️ Request to generate all subscriptions PDF");
      ByteArrayInputStream pdfStream = this.pdfService.generateAllSubscriptionCommunePdf();
      byte[] pdfBytes = pdfStream.readAllBytes();
      log.info("✅ All subscriptions PDF generated, size: {} bytes", pdfBytes.length);

      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subscription.pdf")
              .contentType(MediaType.APPLICATION_PDF)
              .body(pdfBytes);
  }

  // List subscriptions with dynamic sorting
  @Operation(
      summary="REST API to print subscription PDF by sort into ETAT CIVIL APP",
      description = "REST API to print subscription PDF by sort inside ETAT CIVIL App "
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
      log.info("➡️ Request to list subscriptions with sorting. page: {}, size: {}, sortBy: {}, sortDir: {}, fromDate: {}",
              page, size, sortBy, sortDir, fromDate);

      Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Page<Subscription> subscriptionsPage;

      if (fromDate != null && !fromDate.isEmpty()) {
          LocalDate startDate = LocalDate.parse(fromDate); // Just parse the date
          LocalDateTime startDateTime = startDate.atStartOfDay(); // Convert to LocalDateTime
          subscriptionsPage = pdfService.getSubscriptionsByCommuneFromDate(
                  admin.getCommune(), startDateTime, page, size, sortBy, sortDir);
      } else {
          subscriptionsPage = pdfService.getSubscriptionsByCommune(
                  admin.getCommune(), page, size, sortBy, sortDir);
      }
      log.info("✅ Subscriptions fetched, total elements: {}", subscriptionsPage.getTotalElements());
      return ResponseEntity.ok(subscriptionsPage);
  }

  // ======================================================================================================
  // Generate a PDF with all birth certificates
  @Operation(
      summary="REST API to print all birth certificate PDFs into ETAT CIVIL APP",
      description = "REST API to print all birth certificates inside ETAT CIVIL App "
  )
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/allbirthcertifcatespdfprinting")
  public ResponseEntity<byte[]> getAllBirthCertificatesPdf() throws IOException {
      log.info("➡️ Request to generate all birth certificates PDF");
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
      log.info("✅ All birth certificates PDF generated, size: {} bytes", pdfBytes.length);

      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_birth_certificates.pdf")
              .contentType(MediaType.APPLICATION_PDF)
              .body(pdfBytes);
  }

}
