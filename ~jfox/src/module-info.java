module jfox {
	exports jfox.exception;
	exports jfox.context;
	exports jfox.jasperreports;
	exports jfox.javafx.control;
	exports jfox.javafx.util;
	exports jfox.javafx.util.converter;
	exports jfox.javafx.validation;
	exports jfox.javafx.view;
	exports jfox.jdbc;

	requires transitive jakarta.annotation;
	requires transitive jakarta.inject;

	requires transitive java.compiler;
	requires transitive java.logging;
	requires transitive java.sql;
	requires transitive java.desktop;
	
	requires transitive javafx.base;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.swing;
	requires transitive jasperreports;

}