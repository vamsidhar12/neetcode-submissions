class Solution {
    public String addBinary(String a, String b) {
        int len1 = a.length() - 1, len2 = b.length() - 1;
        int carry = 0;
        StringBuilder sb = new StringBuilder();
        while(len1 >= 0 || len2 >= 0 || carry != 0) {
            int d1 = len1 >= 0 ? a.charAt(len1) - '0' : 0;
            int d2 = len2 >= 0 ? b.charAt(len2) - '0' : 0;
            int sum = d1 + d2 + carry;
            carry = sum > 1 ? 1 : 0;
            sum %= 2;
            sb.append(sum);
            len1--;
            len2--;
        }
        return sb.reverse().toString();
    }
}