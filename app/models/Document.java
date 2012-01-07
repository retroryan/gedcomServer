package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Document extends Model
{
    public String fileName;
    public String comment;
    public String contentType;
}
