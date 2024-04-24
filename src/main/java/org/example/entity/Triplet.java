package org.example.entity;

public class Triplet {
    private String start;
    private String relation;
    private String end;

    public Triplet(String start, String relation, String end) {
        this.start = start;
        this.relation = relation;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "(" + this.start + "," + this.relation + "," + this.end + ")\n";
    }
}


