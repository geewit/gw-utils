package io.geewit.utils.javafx.base;

import io.geewit.utils.javafx.base.scene.GwScene;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Utility dialog for previewing images with zoom controls.
 */
public final class ImagePreviewDialog {

    private static final double DEFAULT_WIDTH = 500;
    private static final double DEFAULT_HEIGHT = 400;
    private static final double ZOOM_STEP = 0.25;
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 4.0;

    private ImagePreviewDialog() {
    }

    public static void show(Window owner, byte[] imageData, String title) {
        if (imageData == null || imageData.length == 0) {
            return;
        }
        Image image = new Image(new ByteArrayInputStream(imageData));
        if (image.isError()) {
            Throwable cause = image.getException();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            String message = cause != null ? cause.getMessage() : "Unable to load image";
            throw new IllegalArgumentException(message, cause);
        }
        show(owner, image, title);
    }

    public static void show(Window owner, Image image, String title) {
        Objects.requireNonNull(image, "image must not be null");

        Stage stage = new Stage();
        UniqueDialogManager.show(stage, ImagePreviewDialog.class, title, () -> {
            if (owner != null) {
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
            } else {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            stage.setTitle(title != null && !title.isBlank() ? title : "Image Preview");
            stage.setResizable(true);

            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            DoubleProperty zoom = new SimpleDoubleProperty(1.0);
            imageView.scaleXProperty().bind(zoom);
            imageView.scaleYProperty().bind(zoom);

            StackPane imageContainer = new StackPane(imageView);
            imageContainer.setAlignment(Pos.CENTER);

            ScrollPane scrollPane = new ScrollPane(imageContainer);
            scrollPane.setPannable(true);
            scrollPane.setFitToWidth(false);
            scrollPane.setFitToHeight(false);
            scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                if (event.isControlDown()) {
                    adjustZoom(zoom, event.getDeltaY());
                    event.consume();
                }
            });

            Button zoomOutButton = new Button("-");
            zoomOutButton.setFocusTraversable(false);
            zoomOutButton.setOnAction(_ -> adjustZoom(zoom, -1));

            Button zoomInButton = new Button("+");
            zoomInButton.setFocusTraversable(false);
            zoomInButton.setOnAction(_ -> adjustZoom(zoom, 1));

            Button resetZoomButton = new Button("100%");
            resetZoomButton.setFocusTraversable(false);
            resetZoomButton.setOnAction(_ -> zoom.set(1.0));

            NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.getDefault());
            percentFormat.setMinimumFractionDigits(0);
            percentFormat.setMaximumFractionDigits(0);

            Label zoomLabel = new Label(percentFormat.format(zoom.get()));
            zoom.addListener((_, _, newVal) -> zoomLabel.setText(percentFormat.format(newVal.doubleValue())));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            ToolBar toolBar = new ToolBar(zoomOutButton, zoomInButton, resetZoomButton, new Separator(), spacer, zoomLabel);
            toolBar.setPadding(new Insets(5));

            BorderPane root = new BorderPane(scrollPane);
            root.setTop(toolBar);
            root.setPadding(new Insets(10));
            root.getStyleClass().addAll("container", "dialog");
            String bootstrap = Objects.requireNonNull(ImagePreviewDialog.class.getResource("/styles/com/geewit/javafx/bootstrap.css"))
                    .toExternalForm();
            root.getStylesheets().add(bootstrap);
            BootstrapFXHelper.applyBootstrapFX(root);

            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            if (!Double.isFinite(imageWidth) || imageWidth <= 0) {
                imageWidth = DEFAULT_WIDTH;
            }
            if (!Double.isFinite(imageHeight) || imageHeight <= 0) {
                imageHeight = DEFAULT_HEIGHT;
            }

            double viewportWidth = Math.min(imageWidth, DEFAULT_WIDTH);
            double viewportHeight = Math.min(imageHeight, DEFAULT_HEIGHT);
            scrollPane.setPrefViewportWidth(viewportWidth);
            scrollPane.setPrefViewportHeight(viewportHeight);

            Scene scene = new GwScene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.showAndWait();
        });
    }

    private static void adjustZoom(DoubleProperty zoom, double direction) {
        if (direction == 0) {
            return;
        }
        double factor = direction > 0 ? 1 + ZOOM_STEP : 1 - ZOOM_STEP;
        double newZoom = zoom.get() * factor;
        newZoom = clamp(newZoom, MIN_ZOOM, MAX_ZOOM);
        zoom.set(newZoom);
    }

    private static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }
}

