public class AA {
    public static void main(String[] args) {
        int[] aa = {3, 4, 2, 3}; //false
        int[] bb = {4,2,1}; //false
        int[] cc = {2,3,3,2,4}; //true
        int[] dd = {4,2,3}; //true
        int[] ee = {-1,4,2,3}; //true
       System.out.println(checkPossibility(aa));
        System.out.println(checkPossibility(bb));
        System.out.println(checkPossibility(cc));
        System.out.println(checkPossibility(dd));
        System.out.println(checkPossibility(ee));
    }

    public static boolean checkPossibility(int[] nums) {
        if (nums == null) return false;
        int length = nums.length;
        if (length < 2) return true;
        int flag = 0;
        for (int i = 1; i < length; i++) {
            if (nums[i - 1] <= nums[i]) {
                continue;
            }
            flag ++;
            if(flag >1){
                return false;
            }else if (flag ==1 && (i-1 == 0 || i == length-1)) {
                nums[i-1] = nums[i];
                continue;
            }
            if (nums[i-1]>nums[i+1]){
                nums[i-1] = nums[i-2];
            }else{
                nums[i] = nums[i+1];
            }
            i--;

        }
        return true;
    }
}
