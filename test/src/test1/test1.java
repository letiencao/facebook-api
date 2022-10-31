package test1;

public class test1 {
    //Khoi tao 3 so dau tien > 0
    long[] arrNum = new long[100];
    arrNum[0] = 1;
    arrNum[1] = 2;
    arrNum[2] = 1;

    //Tạo ra 97 số còn lại theo quy tắc A[i] = A[i-1] * A[i-2] * A[i-3];
        for(int i = 3;i < arrNum.length;i++){
        arrNum[i] = arrNum[i-1] * arrNum[i-2] * arrNum[i-3];
    }
        for(int i = 0; i < arrNum.length;i++){
        System.out.println(arrNum[i]);
    }
    //Tính giá trị trung bình của 100 số
    long sum = 0;
        for(int i = 0; i < arrNum.length;i++){
        sum = sum + arrNum[i];
    }
    double giaTriTrungBinh = sum/100;
        System.out.println(giaTriTrungBinh);
    //Tim 2 so gan nhat gia tri trung binh
    //Tim so ben trai
    long numberLeft = 0;
    int indexRight = 0;
        for(int i = 0;i < arrNum.length;i++){
        if(giaTriTrungBinh - (double)arrNum[i] < 0){
            numberLeft = arrNum[i-1];
            indexRight = i;
        }
    }
        System.out.println("numberLeft = " + numberLeft);
    //Tim so ben phai
    long numberRight = arrNum[indexRight];
        System.out.println("numberRight = " + numberRight);
}
