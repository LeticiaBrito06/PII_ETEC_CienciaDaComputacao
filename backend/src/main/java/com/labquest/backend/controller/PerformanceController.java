package com.labquest.backend.controller;

import com.labquest.backend.dtos.performance.PerformanceResponse;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.security.AuthenticatedUser;
import com.labquest.backend.service.CurrentUserService;
import com.labquest.backend.service.PerformanceReportService;
import com.labquest.backend.service.PerformanceService;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final PerformanceReportService performanceReportService;
    private final CurrentUserService currentUserService;

    public PerformanceController(
            PerformanceService performanceService,
            PerformanceReportService performanceReportService,
            CurrentUserService currentUserService) {
        this.performanceService = performanceService;
        this.performanceReportService = performanceReportService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<PerformanceResponse> listAll() {
        currentUserService.requireRole(UserType.PROFESSOR);
        return performanceService.listAll();
    }

    @GetMapping(value = "/turma/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportClassPdf() {
        currentUserService.requireRole(UserType.PROFESSOR);
        byte[] pdf = performanceReportService.gerarPdfTurma();
        return pdfResponse(pdf, "Relatorio_Turma.pdf");
    }

    @GetMapping("/{alunoId}")
    public PerformanceResponse getByStudent(@PathVariable Integer alunoId) {
        return performanceService.getByStudentId(alunoId);
    }

    @GetMapping(value = "/{alunoId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportStudentPdf(@PathVariable Integer alunoId) {
        requireOwnStudentOrTeacher(alunoId);
        byte[] pdf = performanceReportService.gerarPdfAluno(alunoId);
        return pdfResponse(pdf, performanceReportService.nomeArquivoAluno(alunoId));
    }

    @PutMapping("/{alunoId}/refresh")
    public PerformanceResponse refresh(@PathVariable Integer alunoId) {
        return performanceService.refreshAndGet(alunoId);
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String nomeArquivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(nomeArquivo).build().toString())
                .body(pdf);
    }

    private void requireOwnStudentOrTeacher(Integer alunoId) {
        AuthenticatedUser usuarioAtual = currentUserService.getCurrentUser();
        if (usuarioAtual.tipo() == UserType.PROFESSOR || usuarioAtual.id().equals(alunoId)) {
            return;
        }

        throw new ApiException(HttpStatus.FORBIDDEN, "Perfil sem permissao para esta operacao.");
    }
}
