package com.inventario.services;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.inventario.exception.HttpException;
import com.inventario.models.DetalleVenta;
import com.inventario.models.Venta;
import com.inventario.repository.VentaRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TicketPdfService {

    private final VentaRepository ventaRepository;

    public TicketPdfService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    /**
     * Genera un PDF con formato de ticket térmico (80mm de ancho)
     *
     * @param ventaId ID de la venta
     * @return ByteArray del PDF generado
     * @throws HttpException
     */
    public byte[] generarTicketVenta(Integer ventaId) throws HttpException {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new HttpException("Venta no encontrada"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Crear documento con tamaño de impresora térmica (80mm x 297mm)
            Rectangle pageSize = new Rectangle(227, 841);
            Document document = new Document(pageSize, 10, 10, 10, 10);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Fuentes
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 8);
            Font fontSmall = new Font(Font.FontFamily.HELVETICA, 7);

            // Encabezado
            Paragraph title = new Paragraph("TICKET DE VENTA", fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            // Separador
            Paragraph separator = new Paragraph("========================", fontSmall);
            separator.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(separator);

            // Información de venta
            Paragraph codigoP = new Paragraph("Código: " + venta.getCodigoVenta(), fontNormal);
            document.add(codigoP);

            Paragraph fechaP = new Paragraph("Fecha: " + venta.getFecha().toString(), fontSmall);
            document.add(fechaP);

            String cliente = venta.getCliente() != null ? venta.getCliente().getNombre()
                    : venta.getNombreCliente();
            Paragraph clienteP = new Paragraph("Cliente: " + cliente, fontSmall);
            document.add(clienteP);

            Paragraph tipoPagoP = new Paragraph(
                    "Pago: " + venta.getTipoPago().getDescripcion(), fontSmall);
            document.add(tipoPagoP);

            document.add(new Paragraph(" "));

            // Tabla de detalles
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.setSpacingAfter(5);

            // Encabezados
            addTableHeader(table, "Item", fontSmall);
            addTableHeader(table, "Cant.", fontSmall);
            addTableHeader(table, "P.U.", fontSmall);
            addTableHeader(table, "Total", fontSmall);

            // Filas
            int itemNum = 1;
            for (DetalleVenta detalle : venta.getDetalleVentas()) {
                addTableCell(table, String.valueOf(itemNum), fontSmall);
                addTableCell(table, String.valueOf(detalle.getCantidad()), fontSmall);
                addTableCell(table, String.format("%.2f", detalle.getLote().getPrecioVenta()), fontSmall);
                addTableCell(table, String.format("%.2f", detalle.getSubtotal()), fontSmall);
                itemNum++;
            }

            document.add(table);

            // Separador
            document.add(separator);

            // Total
            Paragraph totalP = new Paragraph("TOTAL: $" + String.format("%.2f", venta.getTotal()), fontTitle);
            totalP.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(totalP);

            // Crédito
            if (venta.getEsCredito() != null && venta.getEsCredito()) {
                Paragraph creditoP = new Paragraph(
                        "Monto Crédito: $" + String.format("%.2f", venta.getMontoCredito()), fontSmall);
                creditoP.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(creditoP);
            }

            // Pie
            document.add(new Paragraph(" "));
            Paragraph footerP = new Paragraph("¡Gracias por su compra!", fontSmall);
            footerP.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(footerP);

            Paragraph estadoP = new Paragraph("Estado: " + venta.getEstado().getDescripcion(), fontSmall);
            estadoP.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(estadoP);

            document.close();

            log.info("PDF de venta generado para: " + venta.getCodigoVenta());
            return baos.toByteArray();

        } catch (DocumentException e) {
            log.error("Error al generar PDF para venta: " + ventaId, e);
            throw new HttpException("Error al generar PDF: " + e.getMessage());
        }
    }

    /**
     * Genera un PDF con formato de devolución
     *
     * @param devolucionId ID de la devolución
     * @return ByteArray del PDF generado
     * @throws HttpException
     */
    public byte[] generarTicketDevolucion(Integer devolucionId) throws HttpException {
        // similar al anterior pero para devoluciones
        // será implementado en una segunda iteración si es necesario
        throw new HttpException("Funcionalidad no implementada aún");
    }

    /**
     * Agrega encabezado a la tabla
     */
    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(200, 200, 200));
        table.addCell(cell);
    }

    /**
     * Agrega celda a la tabla
     */
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
