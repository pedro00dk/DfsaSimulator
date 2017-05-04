package gui;

import estimator.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import simulator.SimulationResult;
import simulator.Simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SimulatorScreen {

    @FXML
    private ListView<Estimator> estimatorChooser;

    @FXML
    private TextField initialTagCount;

    @FXML
    private TextField maxTagCount;

    @FXML
    private TextField tagCountIncrement;

    @FXML
    private TextField simulationsPerCount;

    //

    @FXML
    private ComboBox<String> showInCharts;

    @FXML
    private TextArea logArea;

    @FXML
    private CheckBox parallel;

    //

    @FXML
    private LineChart<Number, Number> slotsPerTagCount;

    @FXML
    private LineChart<Number, Number> timePerTagCount;

    @FXML
    private LineChart<Number, Number> idleSlotsPerTagCount;

    @FXML
    private LineChart<Number, Number> collisionSlotsPerTagCount;

    @FXML
    private void initialize() {
        estimatorChooser.setItems(FXCollections.observableArrayList(
                new LowerBound(64),
                new Schoute(64),
                new EomLee(64, 1e-3f),
                new QAlgorithm(6, 0.1f),
                new QAlgorithm(6, 0.2f),
                new QAlgorithm(6, 0.3f),
                new QAlgorithm(6, 0.4f),
                new QAlgorithm(6, 0.5f),
                new LowerBound(128),
                new Schoute(128),
                new EomLee(128, 1e-3f),
                new QAlgorithm(7, 0.1f),
                new QAlgorithm(7, 0.2f),
                new QAlgorithm(7, 0.3f),
                new QAlgorithm(7, 0.4f),
                new QAlgorithm(7, 0.5f)
        ));
        estimatorChooser.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        estimatorChooser.getSelectionModel().selectAll();
        //
        initialTagCount.textProperty().addListener(
                (observable, oldValue, newValue) -> initialTagCount.setText(newValue.matches("\\d*") ? newValue : oldValue)
        );
        initialTagCount.setText(Integer.toString(100));
        tagCountIncrement.textProperty().addListener(
                (observable, oldValue, newValue) -> tagCountIncrement.setText(newValue.matches("\\d*") ? newValue : oldValue)
        );
        tagCountIncrement.setText(Integer.toString(50));
        maxTagCount.textProperty().addListener(
                (observable, oldValue, newValue) -> maxTagCount.setText(newValue.matches("\\d*") ? newValue : oldValue)
        );
        maxTagCount.setText(Integer.toString(1000));
        simulationsPerCount.textProperty().addListener(
                (observable, oldValue, newValue) -> simulationsPerCount.setText(newValue.matches("\\d*") ? newValue : oldValue)
        );
        simulationsPerCount.setText(Integer.toString(10));
        //
        showInCharts.setItems(FXCollections.observableArrayList("Average", "Min", "Max"));
        showInCharts.getSelectionModel().selectFirst();
        //
        ((NumberAxis) slotsPerTagCount.getXAxis()).setForceZeroInRange(false);
        ((NumberAxis) timePerTagCount.getXAxis()).setForceZeroInRange(false);
        ((NumberAxis) idleSlotsPerTagCount.getXAxis()).setForceZeroInRange(false);
        ((NumberAxis) collisionSlotsPerTagCount.getXAxis()).setForceZeroInRange(false);
        slotsPerTagCount.setCursor(Cursor.CROSSHAIR);
        timePerTagCount.setCursor(Cursor.CROSSHAIR);
        idleSlotsPerTagCount.setCursor(Cursor.CROSSHAIR);
        collisionSlotsPerTagCount.setCursor(Cursor.CROSSHAIR);
        slotsPerTagCount.setCreateSymbols(false);
        timePerTagCount.setCreateSymbols(false);
        idleSlotsPerTagCount.setCreateSymbols(false);
        collisionSlotsPerTagCount.setCreateSymbols(false);
    }

    @FXML
    private void simulateOnAction() {
        logArea.setText("");

        slotsPerTagCount.setData(FXCollections.observableArrayList());
        timePerTagCount.setData(FXCollections.observableArrayList());
        idleSlotsPerTagCount.setData(FXCollections.observableArrayList());
        collisionSlotsPerTagCount.setData(FXCollections.observableArrayList());
        logArea.appendText("Cleaning chart data\n");

        List<Estimator> estimatorsToSimulate = estimatorChooser.getSelectionModel().getSelectedItems();

        int initialTagCount = 100;
        try {
            initialTagCount = Integer.parseInt(this.initialTagCount.getText());
        } catch (NumberFormatException ignored) {
            logArea.appendText("Initial tag count format error\n");
            logArea.appendText("Setting default value: " + initialTagCount + "\n");
        }
        int maxTagCount = 1000;
        try {
            maxTagCount = Integer.parseInt(this.maxTagCount.getText());
        } catch (NumberFormatException ignored) {
            logArea.appendText("Max tag count format error\n");
            logArea.appendText("Setting default value: " + maxTagCount + "\n");
        }
        int tagCountIncrement = 50;
        try {
            tagCountIncrement = Integer.parseInt(this.tagCountIncrement.getText());
        } catch (NumberFormatException ignored) {
            logArea.appendText("Tag count inc format error\n");
            logArea.appendText("Setting default value: " + tagCountIncrement + "\n");
        }
        int simulationsPerCount = 10;
        try {
            simulationsPerCount = Integer.parseInt(this.simulationsPerCount.getText());
        } catch (NumberFormatException ignored) {
            logArea.appendText("Simulations per count format error\n");
            logArea.appendText("Setting default value: " + simulationsPerCount + "\n");
        }
        //
        for (Estimator estimator : estimatorsToSimulate) {
            logArea.appendText("Simulating estimator " + estimator.getName() + "\n");
            float startTime = System.nanoTime();
            plotSimulationData(calculateSimulationResults(estimator, initialTagCount, maxTagCount, tagCountIncrement, simulationsPerCount));
            float endTime = System.nanoTime();
            logArea.appendText("Total time: " + ((endTime - startTime) / 1000000.0) + "(ms)\n\n");
        }
    }

    private List<SimulationResult> calculateSimulationResults(Estimator estimator, int initialTagCount, int maxTagCount, int tagCountIncrement, int simulationCount) {
        List<SimulationResult> relevantSimulationResults = new ArrayList<>();
        String relevantDataLabel = showInCharts.getSelectionModel().getSelectedItem();
        if (!parallel.isSelected()) {
            for (int tagCount = initialTagCount; tagCount <= maxTagCount; tagCount += tagCountIncrement) {
                System.out.println("Simulating " + estimator.toString() + " tag count: " + tagCount);
                relevantSimulationResults.add(calculateRelevantData(calculateSimulationResultsPerTagCount(estimator, tagCount, simulationCount), relevantDataLabel));
            }
        } else {
            IntStream.iterate(initialTagCount, tagCount -> tagCount + tagCountIncrement)
                    .limit((maxTagCount - initialTagCount) / tagCountIncrement + 1)
                    .parallel()
                    .forEach(tagCount ->
                            relevantSimulationResults.add(calculateRelevantData(calculateSimulationResultsPerTagCount(estimator, tagCount, simulationCount), relevantDataLabel)));
        }
        return relevantSimulationResults;
    }

    private List<SimulationResult> calculateSimulationResultsPerTagCount(Estimator estimator, int tagCount, int simulationCount) {
        List<SimulationResult> results = new ArrayList<>();
        if (!parallel.isSelected()) {
            for (int i = 0; i < simulationCount; i++) {
                results.add(Simulator.simulate(estimator.copy(), tagCount));
            }
        } else {
            IntStream.range(0, simulationCount).parallel()
                    .forEach(simulation -> results.add(Simulator.simulate(estimator.copy(), tagCount)));
        }
        return results;
    }

    private SimulationResult calculateRelevantData(List<SimulationResult> simulationResults, String relevantData) {
        switch (relevantData) {
            case "Average":
                return SimulationResult.average(simulationResults);
            case "Min":
                return SimulationResult.min(simulationResults);
            case "Max":
                return SimulationResult.max(simulationResults);
        }
        return null;
    }

    private void plotSimulationData(List<SimulationResult> simulationResults) {
        String estimatorName = simulationResults.get(0).estimator.getName();
        XYChart.Series<Number, Number> slotsPerTagCountSeries;
        XYChart.Series<Number, Number> timePerTagCountSeries;
        XYChart.Series<Number, Number> idleSlotsPerTagCountSeries;
        XYChart.Series<Number, Number> collisionSlotsPerTagCountSeries;
        slotsPerTagCountSeries = new XYChart.Series<>();
        timePerTagCountSeries = new XYChart.Series<>();
        idleSlotsPerTagCountSeries = new XYChart.Series<>();
        collisionSlotsPerTagCountSeries = new XYChart.Series<>();
        slotsPerTagCountSeries.setName(estimatorName);
        timePerTagCountSeries.setName(estimatorName);
        idleSlotsPerTagCountSeries.setName(estimatorName);
        collisionSlotsPerTagCountSeries.setName(estimatorName);
        slotsPerTagCount.getData().add(slotsPerTagCountSeries);
        timePerTagCount.getData().add(timePerTagCountSeries);
        idleSlotsPerTagCount.getData().add(idleSlotsPerTagCountSeries);
        collisionSlotsPerTagCount.getData().add(collisionSlotsPerTagCountSeries);

        int seriesIndex = slotsPerTagCount.getData().size() - 1;

        for (SimulationResult simulationResult : simulationResults) {
            XYChart.Data<Number, Number> slotsPerTagCountSeriesData = new XYChart.Data<>(simulationResult.tagCount, simulationResult.createdSlots);
            slotsPerTagCountSeriesData.setNode(new ChartDataNode(simulationResult.createdSlots, seriesIndex));
            slotsPerTagCountSeries.getData().add(slotsPerTagCountSeriesData);

            XYChart.Data<Number, Number> timePerTagCountSeriesData = new XYChart.Data<>(simulationResult.tagCount, Math.log(simulationResult.executionTime + 1));
            timePerTagCountSeriesData.setNode(new ChartDataNode(simulationResult.executionTime, seriesIndex));
            timePerTagCountSeries.getData().add(timePerTagCountSeriesData);

            XYChart.Data<Number, Number> idleSlotsPerTagCountSeriesData = new XYChart.Data<>(simulationResult.tagCount, simulationResult.idleSlots);
            idleSlotsPerTagCountSeriesData.setNode(new ChartDataNode(simulationResult.idleSlots, seriesIndex));
            idleSlotsPerTagCountSeries.getData().add(idleSlotsPerTagCountSeriesData);

            XYChart.Data<Number, Number> collisionSlotsPerTagCountSeriesData = new XYChart.Data<>(simulationResult.tagCount, simulationResult.collisionSlots);
            collisionSlotsPerTagCountSeriesData.setNode(new ChartDataNode(simulationResult.collisionSlots, seriesIndex));
            collisionSlotsPerTagCountSeries.getData().add(collisionSlotsPerTagCountSeriesData);
        }
    }

    private class ChartDataNode extends StackPane {

        private ChartDataNode(Number value, int defaultColorIndex) {
            setPrefSize(8, 8);

            Label hoverLabel = new Label(value + "");
            hoverLabel.getStyleClass().addAll("default-color" + defaultColorIndex, "chart-line-symbol", "chart-series-line");
            hoverLabel.setStyle("-fx-font-size: 10;");
            hoverLabel.setTextFill(Color.FIREBRICK);
            hoverLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

            setOnMouseEntered(mouseEvent -> {
                getChildren().setAll(hoverLabel);
                setCursor(Cursor.NONE);
                toFront();
            });

            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });
        }
    }
}
