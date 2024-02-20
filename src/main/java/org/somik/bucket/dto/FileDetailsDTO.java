package org.somik.bucket.dto;

public class FileDetailsDTO {

    public FileDetailsDTO() {
        super();
    }

    @Override
    public String toString() {
        return "{" +
                " name='" + getName() + "'" +
                ", size='" + getSize() + "'" +
                ", modified='" + getModified() + "'" +
                ", url='" + getUrl() + "'" +
                "}";
    }

    private String name;
    private String size;
    private String modified;
    private String url;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getModified() {
        return this.modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
