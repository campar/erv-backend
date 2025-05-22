package com.example.erv.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.example.erv.config.AppConfig;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

@Service
public class ReportService {
    private static final Logger logger = LogManager.getLogger(ReportService.class);
    private static final String RESOURCE_PATH = "/reports/compiled/";
    private static final String EXTENSION = ".jasper";
    private static final String REPORT_STRING = "Report";

    public byte[] createPDFReport(List<?> reportDataList, Map<String, Object> parameters, String reportPath) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            parameters.put("timezoneId", AppConfig.getTimezoneId());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }

    public byte[] createPDFReportWithSubReport(List<?> reportDataList, Map<String, Object> parameters, String reportPath) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            InputStream jasperSubreportStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath.replace(REPORT_STRING, "SubReport") + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(jasperSubreportStream);
            parameters.put("subreportParameter", jasperSubReport);
            parameters.put("timezoneId", AppConfig.getTimezoneId());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }

    public byte[] createPDFReportWithSubReports(List<?> reportDataList, Map<String, Object> parameters, String reportPath, List<String> subReportSufixNames) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameters.put("timezoneId", AppConfig.getTimezoneId());

            for (String subReportSuffixName : subReportSufixNames) {
                InputStream jasperSubreportStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath.replace(REPORT_STRING, subReportSuffixName) + EXTENSION);
                if (jasperSubreportStream == null) {
                    throw new FileNotFoundException("Subreport file not found: /reports/compiled/" + reportPath.replace(REPORT_STRING, subReportSuffixName) + EXTENSION);
                }
                JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(jasperSubreportStream);
                parameters.put(lowerFirstLetter(subReportSuffixName) + "Parameter", jasperSubReport);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }

    public byte[] createCsvReport(List<?> reportDataList, Map<String, Object> parameters, String reportPath) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return exportToCsv(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }

    public byte[] createCsvReportWithSubReport(List<?> reportDataList, Map<String, Object> parameters, String reportPath) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            InputStream jasperSubreportStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath.replace(REPORT_STRING, "SubReport") + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(jasperSubreportStream);
            parameters.put("subreportParameter", jasperSubReport);
            parameters.put("timezoneId", AppConfig.getTimezoneId());
            
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return exportToCsv(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }

    public byte[] createCsvReportWithSubReports(List<?> reportDataList, Map<String, Object> parameters, String reportPath, List<String> subReportSuffixNames) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameters.put("timezoneId", AppConfig.getTimezoneId());
            
            for (String subReportSuffixName : subReportSuffixNames) {
                InputStream jasperSubreportStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath.replace(REPORT_STRING, subReportSuffixName) + EXTENSION);
                if (jasperSubreportStream == null) {
                    throw new FileNotFoundException("Subreport file not found: /reports/compiled/" + reportPath.replace(REPORT_STRING, subReportSuffixName) + EXTENSION);
                }
                JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(jasperSubreportStream);
                parameters.put(lowerFirstLetter(subReportSuffixName) + "Parameter", jasperSubReport);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return exportToCsv(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }
    
    public byte[] createXlsxReportWithSubReport(List<?> reportDataList, Map<String, Object> parameters, String reportPath) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            InputStream jasperSubreportStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath.replace(REPORT_STRING, "SubReport") + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(jasperSubreportStream);
            parameters.put("subreportParameter", jasperSubReport);
            parameters.put("timezoneId", AppConfig.getTimezoneId());
            
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return exportToXlsx(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }
    
    public byte[] createXlsxReportWithSubReports(List<?> reportDataList, Map<String, Object> parameters, String reportPath, List<String> subReportSufixNames) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath + EXTENSION);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameters.put("timezoneId", AppConfig.getTimezoneId());

            for (String subReportSuffixName : subReportSufixNames) {
                InputStream jasperSubreportStream = this.getClass().getResourceAsStream(RESOURCE_PATH + reportPath.replace(REPORT_STRING, subReportSuffixName) + EXTENSION);
                if (jasperSubreportStream == null) {
                    throw new FileNotFoundException("Subreport file not found: /reports/compiled/" + reportPath.replace(REPORT_STRING, subReportSuffixName) + EXTENSION);
                }
                JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(jasperSubreportStream);
                parameters.put(lowerFirstLetter(subReportSuffixName) + "Parameter", jasperSubReport);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return exportToXlsx(jasperPrint);
        } catch (Exception e) {
            logger.error(e);
            return new byte[0];
        }
    }

    private byte[] exportToCsv(JasperPrint jasperPrint) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//        	byteArrayOutputStream.write(0xEF);
//            byteArrayOutputStream.write(0xBB);
//            byteArrayOutputStream.write(0xBF);
        	JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(byteArrayOutputStream));
            SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
            configuration.setFieldDelimiter(",");
            configuration.setWriteBOM(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Error exporting report to CSV: ", e);
            return new byte[0];
        }
    }
    
    private byte[] exportToXlsx(JasperPrint jasperPrint) {
    	try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
    		JRXlsxExporter exporter = new JRXlsxExporter();
    		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
    		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
    		exporter.exportReport();
    		return byteArrayOutputStream.toByteArray();
    	}catch (Exception e) {
    		logger.error("Error exporting report to Xlsx: ", e);
            return new byte[0];
		}
    }

    
    public static String lowerFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }
} 
