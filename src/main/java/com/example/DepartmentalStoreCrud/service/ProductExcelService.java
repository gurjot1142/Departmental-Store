package com.example.DepartmentalStoreCrud.service;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service
@Slf4j
public class ProductExcelService {

    /**
     * Autowired ProductInventoryRepository
     */
    @Autowired
    private ProductInventoryRepository productRepo;

    /**
     * To validate the file format
     *
     * @param file - input file
     * @return - True/False
     */
    private boolean checkExcelFormat(final MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        }
        return false;
    }

    /**
     * To convert excel data to product list
     *
     * @param input - Input Stream
     * @return List of products
     */
    private List<ProductInventory> convertExcelToListOfProducts(final InputStream input) throws IOException {
        List<ProductInventory> productList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(input);

        XSSFSheet sheet = workbook.getSheet("productData");

        int rowNumber = 0;
        Iterator<Row> iterator = sheet.iterator();

        while (iterator.hasNext()) {
            Row row = iterator.next();

            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }

            Iterator<Cell> cells = row.iterator();

            int cid = 0;

            ProductInventory p = new ProductInventory();

            while (cells.hasNext()) {
                Cell cell = cells.next();

                switch (cid) {
                    case 0:
                        p.setProductName(cell.getStringCellValue());
                        break;
                    case 1:
                        p.setProductDesc(cell.getStringCellValue());
                        break;
                    case 2:
                        p.setPrice(cell.getNumericCellValue());
                        break;
                    case 3:
                        p.setProductQuantity((int) cell.getNumericCellValue());
                        break;
                    default:
                        break;
                }
                cid++;
            }
            productList.add(p);
        }
        return productList;
    }

    /**
     * To add new products via Excel sheet
     *
     * @param file - Input file
     */
    public void addProductsViaExcel(final MultipartFile file) throws IOException {
        if (checkExcelFormat(file)) {
            List<ProductInventory> products = convertExcelToListOfProducts(file.getInputStream());
            log.info("Products added via excel");
            productRepo.saveAll(products);
        } else {
            log.info("IO exception occured");
            throw new IOException("Invalid file format");
        }
    }
}
