package org.vandeseer.integrationtest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;
import org.vandeseer.TestUtils;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.CellCustom;
import org.vandeseer.easytable.structure.cell.CellImage;
import org.vandeseer.easytable.structure.cell.CellText;

import java.awt.*;
import java.io.IOException;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.vandeseer.TestUtils.createGliderImage;
import static org.vandeseer.TestUtils.createTuxImage;
import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.HorizontalAlignment.RIGHT;
import static org.vandeseer.easytable.settings.VerticalAlignment.BOTTOM;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

public class CellSpanningTest {

    @Test
    public void createDocumentWithTables() throws Exception {
        TestUtils.createAndSaveDocumentWithTables("cellSpanning.pdf",
                createTableWithCellColSpanning(),
                createTableWithCellRowSpanning(),
                createTableWithTwoCellRowSpannings(),
                createTableWithSeveralRowSpannings(),
                createTableWithDifferentAlignmentsInSpannedCells(),
                createTableWithSeveralRowAndCellSpannings(),
                createTableWithImages()
        );
    }

    @Test
    public void createDocumentWithTableOverMultiplePages() throws Exception {
        final PDDocument document = new PDDocument();

        TableDrawer drawer = TableDrawer.builder()
                .table(createVeryLargeTable())
                .startX(50)
                .endY(50F) // note: if not set, table is drawn over the end of the page
                .build();

        do {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            drawer.startY(page.getMediaBox().getHeight() - 50);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                drawer.contentStream(contentStream).draw();
            }
        } while (!drawer.isFinished());

        document.save("target/cellSpanningMultiplePages.pdf");
        document.close();
    }

    private Table createTableWithCellColSpanning() {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(300, 120, 70)
                .fontSize(8)
                .font(HELVETICA);

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Pur").colSpan(2).backgroundColor(Color.YELLOW).horizontalAlignment(CENTER).borderWidth(1).build())
                .add(CellCustom.builder().heightSupplier(() -> 35f).onDrawConsumer(context -> {

                    try {
                        context.contentStream.moveTo(context.x, context.y);
                        context.contentStream.drawLine(context.x, context.y, context.x + 10, context.y + 10);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                }).span(2).build())
                .add(CellText.builder().text("Booz").build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Hey").horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Ho!").build())
                .add(CellText.builder().text("Fu.").backgroundColor(Color.ORANGE).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Bar").horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Baz").colSpan(2).backgroundColor(Color.CYAN).horizontalAlignment(CENTER).borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        return tableBuilder.build();
    }

    private Table createTableWithCellRowSpanning() {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(300, 120, 70)
                .fontSize(8)
                .font(HELVETICA);


        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Pur").backgroundColor(Color.YELLOW).horizontalAlignment(CENTER).borderWidth(1).build())
                .add(CellText.builder().text("Pur").backgroundColor(Color.YELLOW).horizontalAlignment(CENTER).borderWidth(1).build())
                .add(CellText.builder().text("Booz").build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Hey").horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Fu.").rowSpan(2).backgroundColor(Color.ORANGE).borderWidth(1).build())
                .add(CellText.builder().text("Ho!").build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Bar").horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Bar").horizontalAlignment(RIGHT).build())
                .backgroundColor(Color.GREEN)
                .build());

        return tableBuilder.build();
    }

    private Table createTableWithTwoCellRowSpannings() throws Exception {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(300, 120, 70)
                .fontSize(8)
                .font(HELVETICA);


        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("This is\nrow span 2").rowSpan(2).backgroundColor(Color.YELLOW).horizontalAlignment(CENTER).borderWidth(1).build())
                .add(CellText.builder().text("Two").backgroundColor(Color.YELLOW).horizontalAlignment(CENTER).borderWidth(1).build())
                .add(CellText.builder().text("Three").borderWidth(1).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("This is\nrow span 3").rowSpan(3).backgroundColor(Color.ORANGE).borderWidth(1).build())
                .add(CellText.builder().text("Three").borderWidth(1).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("One").horizontalAlignment(RIGHT).borderWidth(1).build())
                .add(CellText.builder().text("Three").horizontalAlignment(RIGHT).borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("One").horizontalAlignment(RIGHT).borderWidth(1).build())
                .add(CellText.builder().text("Three").horizontalAlignment(RIGHT).borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        return tableBuilder.build();
    }

    private Table createTableWithSeveralRowSpannings() {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(300, 120, 70)
                .fontSize(8)
                .font(HELVETICA);

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("This is\nrow span 4").rowSpan(4).borderWidth(1).verticalAlignment(MIDDLE).horizontalAlignment(CENTER).build())
                .add(CellText.builder().text("Pur").backgroundColor(Color.YELLOW).borderWidth(1).horizontalAlignment(CENTER).build())
                .add(CellText.builder().text("Booz").borderWidth(1).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("This is\nrow span 3").rowSpan(3).borderWidth(1).backgroundColor(Color.ORANGE).build())
                .add(CellText.builder().text("Ho!").borderWidth(1).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Three").borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Three").borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("One").borderWidth(1).horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Two").borderWidth(1).horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Three").borderWidth(1).horizontalAlignment(RIGHT).build())
                .backgroundColor(Color.CYAN)
                .build());

        return tableBuilder.build();
    }

    private Table createTableWithDifferentAlignmentsInSpannedCells() {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(300, 120, 70)
                .fontSize(8)
                .font(HELVETICA);

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("This is\nrow span 4").rowSpan(4).borderWidth(1).verticalAlignment(MIDDLE).horizontalAlignment(CENTER).build())
                .add(CellText.builder().text("Pur").backgroundColor(Color.YELLOW).borderWidth(1).horizontalAlignment(CENTER).build())
                .add(CellText.builder().text("Booz").borderWidth(1).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("This is\nrow span 3, bottom aligned").rowSpan(3).borderWidth(1)
                        .backgroundColor(Color.ORANGE).verticalAlignment(BOTTOM).paddingBottom(2).build())
                .add(CellText.builder().text("Ho!").borderWidth(1).build())
                .backgroundColor(Color.BLUE)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Three").borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("Three").borderWidth(1).build())
                .backgroundColor(Color.GREEN)
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().text("One").borderWidth(1).horizontalAlignment(RIGHT).build())
                .add(CellText.builder().text("Two").borderWidth(1).horizontalAlignment(CENTER).build())
                .add(CellText.builder().text("Three").borderWidth(1).horizontalAlignment(CENTER).build())
                .backgroundColor(Color.CYAN)
                .build());

        return tableBuilder.build();
    }

    private Table createTableWithSeveralRowAndCellSpannings() {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(200, 120, 70, 100)
                .fontSize(8)
                .font(HELVETICA);

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().borderWidth(1).text("One").build())
                .add(CellText.builder().borderWidth(1).text("Two").build())
                .add(CellText.builder().borderWidth(1).text("Three").build())
                .add(CellText.builder().borderWidth(1).text("Four").build())
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().borderWidth(1).text("One").build())
                .add(CellText.builder().borderWidth(1).text("Spanning 2x2").colSpan(2).rowSpan(2).build())
                .add(CellText.builder().borderWidth(1).text("Four").build())
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().borderWidth(1).text("One").build())
                .add(CellText.builder().borderWidth(1).text("Four").build())
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellText.builder().borderWidth(1).text("One").build())
                .add(CellText.builder().borderWidth(1).text("Two").build())
                .add(CellText.builder().borderWidth(1).text("Three").build())
                .add(CellText.builder().borderWidth(1).text("Four").build())
                .build());

        return tableBuilder.build();
    }

    private Table createTableWithImages() throws IOException {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(200, 120, 70, 100)
                .horizontalAlignment(CENTER)
                .verticalAlignment(MIDDLE)
                .fontSize(8)
                .font(HELVETICA);

        tableBuilder.addRow(Row.builder()
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createGliderImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createGliderImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createGliderImage()).scale(0.2f).colSpan(2).rowSpan(2).build())
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .build());

        tableBuilder.addRow(Row.builder()
                .add(CellImage.builder().borderWidth(1).image(createGliderImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createTuxImage()).scale(0.1f).build())
                .add(CellImage.builder().borderWidth(1).image(createGliderImage()).scale(0.1f).build())
                .build());

        return tableBuilder.build();
    }

    private Table createVeryLargeTable() {
        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(300, 120, 70)
                .fontSize(12)
                .font(HELVETICA);

        int count = 0;
        for (int i = 0; i < 50; i++) {
            if (i % 2 == 0) {
                tableBuilder.addRow(Row.builder()
                        .add(CellText.builder().text("This is\nrow span 4").rowSpan(4).borderWidth(1).verticalAlignment(MIDDLE).horizontalAlignment(CENTER).build())
                        .add(CellText.builder().text("Pur").backgroundColor(Color.YELLOW).borderWidth(1).horizontalAlignment(CENTER).build())
                        .add(CellText.builder().text("Booz").borderWidth(1).build())
                        .backgroundColor(Color.BLUE)
                        .build());

                tableBuilder.addRow(Row.builder()
                        .add(CellText.builder().text("This is\nrow span 3").rowSpan(3).borderWidth(1)
                                .backgroundColor(Color.ORANGE).verticalAlignment(BOTTOM).build())
                        .add(CellText.builder().text("Ho!").borderWidth(1).build())
                        .backgroundColor(Color.BLUE)
                        .build());

                tableBuilder.addRow(Row.builder()
                        .add(CellText.builder().text("Three").borderWidth(1).build())
                        .backgroundColor(Color.GREEN)
                        .build());

                tableBuilder.addRow(Row.builder()
                        .add(CellText.builder().text("Three").borderWidth(1).build())
                        .backgroundColor(Color.GREEN)
                        .build());

                tableBuilder.addRow(Row.builder()
                        .add(CellText.builder().text("Spanning 2 cols").colSpan(2).borderWidth(1).build())
                        .add(CellText.builder().text("Three").borderWidth(1).horizontalAlignment(CENTER).build())
                        .backgroundColor(Color.CYAN)
                        .build());
            }

            tableBuilder.addRow(Row.builder()
                    .add(CellText.builder().borderWidth(1).text(String.valueOf(count++)).build())
                    .add(CellText.builder().borderWidth(1).text("Two").build())
                    .add(CellText.builder().borderWidth(1).text("Three").build())
                    .build());

            tableBuilder.addRow(Row.builder()
                    .add(CellText.builder().borderWidth(1).text(String.valueOf(count++)).build())
                    .add(CellText.builder().borderWidth(1).text("Spanning 2x2").colSpan(2).rowSpan(2).build())
                    .build());

            tableBuilder.addRow(Row.builder()
                    .add(CellText.builder().borderWidth(1).text(String.valueOf(count++)).build())
                    .build());

            tableBuilder.addRow(Row.builder()
                    .add(CellText.builder().borderWidth(1).text(String.valueOf(count++)).build())
                    .add(CellText.builder().borderWidth(1).text("Two").build())
                    .add(CellText.builder().borderWidth(1).text("Three").build())
                    .build());
        }

        return tableBuilder.build();
    }

}
