public record SClass(int classID, String classType, int yearGroup) {

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
