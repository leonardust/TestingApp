package com.javafortesters.pulp.html;

import com.javafortesters.pulp.domain.groupings.PulpData;
import com.javafortesters.pulp.html.templates.PaginatorRender;
import com.javafortesters.pulp.reader.ResourceReader;
import com.javafortesters.pulp.reporting.PulpReporter;
import com.javafortesters.pulp.reporting.ReportConfig;
import com.javafortesters.pulp.reporting.filtering.BookFilter;
import com.javafortesters.pulp.reporting.reporters.BookReporter;


import java.util.Collection;

public class HtmlReports {
    private final PulpReporter reporter;
    private final PulpData data;
    private ReportConfig reportConfig;

    public HtmlReports(PulpData books) {
        this.data = books;
        reporter = new PulpReporter(books);
        reportConfig = ReportConfig.justStrings();
        reporter.configure(reportConfig);
    }

    public String getBooksAsHtmlList(BookFilter filter) {
        BookReporter bReporter = new BookReporter(reportConfig, data.authors(), data.publishers(), data.series() );
        return  reportCollectionAsLiPage(bReporter.getBooksAsLines(this.data.books().filteredBy(filter)), "Books", "books");
    }

    public String getBooksAsHtmlTable(BookFilter filter) {
        BookReporter bReporter = new BookReporter(reportConfig, data.authors(), data.publishers(), data.series() );
        return reportCollectionAsTablePage(bReporter.getBooksAsTable(this.data.books().filteredBy(filter)), "Books", "books");
    }


    public String getBooksAsHtmlList() {
        return getBooksAsHtmlList(new BookFilter());
    }

    // TODO: this is all getting very hacky - refactor this into the reporter config and allow local adjustments from urls
    public String getAuthorsAsHtmlList(boolean includeFaqs) {
        return reportCollectionAsLiPage(reporter.getAuthorsAsStrings(includeFaqs), "Authors", "authors");
    }

    public String getPublishersAsHtmlList() {
        return reportCollectionAsLiPage(reporter.getPublishersAsStrings(), "Publishers", "publishers");
    }

    public String getYearsAsHtmlList() {
        return reportCollectionAsLiPage(reporter.getYearsAsStrings(), "Years", "years");
    }

    public String getSeriesNamesAsHtmlList() {
        return reportCollectionAsLiPage(reporter.getSeriesNamesAsStrings(), "Series", "series");
    }


    private String reportCollectionAsLiPage(Collection<String> simpleReport, String listOfWhat, String urlArg){

        StringBuilder report = new StringBuilder();

        addHeader(String.format("List of %s", listOfWhat), report);

        startBody(report);

        report.append(String.format("<h1>List of %s</h1>%n", listOfWhat));

        report.append(new HTMLReporter().getAsUl(simpleReport));

        String style = reportConfig.areYearsLinks() ? "navigation" : "static";

        report.append(new PaginatorRender(this.data.books().getPaginationDetails()).renderAsClickable(String.format("%s%s/list/%s",reportConfig.getReportPath(), urlArg, style)));

        addReportList(report);

        endBodyAndPage(report);

        return report.toString();

    }

    private String reportCollectionAsTablePage(String thingsAsTable, String things, String urlArg) {
        StringBuilder report = new StringBuilder();

        addHeader(String.format("Table of %s", things), report);

        startBody(report);

        report.append(String.format("<h1>Table of %s</h1>%n", things));

        report.append(thingsAsTable);

        String style = reportConfig.areYearsLinks() ? "navigation" : "static";
        report.append(new PaginatorRender(this.data.books().getPaginationDetails()).renderAsClickable(String.format("%s%s/table/%s",reportConfig.getReportPath() , urlArg, style)));

        addReportList(report);

        endBodyAndPage(report);

        return report.toString();
    }

    private void endBodyAndPage(StringBuilder report) {
        report.append(HTMLElements.endBody() + HTMLElements.endHTML());
    }

    private void startBody(StringBuilder report) {
        report.append(HTMLElements.startBody());
    }

    public String getIndexPage(){

        StringBuilder report = new StringBuilder();
        addHeader("Pulp App Menu", report);
        startBody(report);

        addReportList(report);

        endBodyAndPage(report);

        return report.toString();
    }

    private void addReportList(StringBuilder report) {

        report.append(new ResourceReader().asString("/web/apps/pulp/page-template/reports-list-widget.html"));

    }


    private void addHeader(String title, StringBuilder report) {
        report.append(HTMLElements.htmlHeadWithTitle(title));
    }


    public HtmlReports configure(ReportConfig reportConfig) {
        this.reportConfig = reportConfig;
        this.reporter.configure(this.reportConfig);
        return this;
    }


    public HtmlReports configurePostFixPath(String linksPostFix) {
        this.reportConfig.setPostFixPath(linksPostFix);
        this.reporter.configure(this.reportConfig);
        return this;
    }


}
