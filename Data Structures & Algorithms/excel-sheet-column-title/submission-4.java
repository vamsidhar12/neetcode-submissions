class Solution {
    public String convertToTitle(int columnNumber) {
        StringBuilder res = new StringBuilder();
        while(columnNumber > 0) {
            columnNumber--;
            int d = (columnNumber) % 26;
            res.append((char)('A' + d));
            columnNumber /= 26;
        }
        return res.reverse().toString();
    }
}