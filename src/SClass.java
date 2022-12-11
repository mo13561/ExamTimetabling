public record SClass(int classID, String classType, int yearGroup) { //record to hold information about a class

    public int getClassID() {
        return classID;
    }

    public String getClassType() {
        return classType;
    }

    public int getYearGroup() {
        return yearGroup;
    }
}
