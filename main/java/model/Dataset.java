package model;

public class Dataset {
    private int id;
    private String name, description, category, format, sourceUrl, status;
    private double sizeMb;

    public Dataset() {}
    public Dataset(int id, String name, String description, String category,
                   String format, double sizeMb, String sourceUrl, String status) {
        this.id = id; this.name = name; this.description = description;
        this.category = category; this.format = format;
        this.sizeMb = sizeMb; this.sourceUrl = sourceUrl; this.status = status;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public double getSizeMb() { return sizeMb; }
    public void setSizeMb(double sizeMb) { this.sizeMb = sizeMb; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}