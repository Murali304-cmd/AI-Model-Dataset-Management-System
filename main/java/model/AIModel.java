package model;

public class AIModel {
    private int id, datasetId;
    private String modelName, version, algorithm;
    private double accuracy;

    public AIModel() {}
    public AIModel(int id, String modelName, String version, String algorithm,
                   double accuracy, int datasetId) {
        this.id = id; this.modelName = modelName; this.version = version;
        this.algorithm = algorithm; this.accuracy = accuracy; this.datasetId = datasetId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public int getDatasetId() { return datasetId; }
    public void setDatasetId(int datasetId) { this.datasetId = datasetId; }
}