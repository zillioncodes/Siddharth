function [ERMS] = predict(matTest, ReleTest,Mean,sd, W, M, lambda) 

m=M;
Ww=W;
warning('off','all');



%validation           


 loopOfV=size(matTest,1);
% i=1;

 n=1;
 nm=1;
 nm1=1;
 while(n<loopOfV+1)
 nm1=1;
 while(nm1<m+1)
indexV(nm1,:)=exp(-(((matTest(n,:)-Mean(nm1,:)).^2)/(2*(sd(nm1)^2))));
nm1=nm1+1;
 end
 nm1=1;
 while(nm1<m+1)
    if nm1==1
    IndexV=indexV(nm1,:);
    else
        testV=IndexV;
        IndexV=[testV,indexV(nm1,:)];
    end;     
    nm1=nm1+1;
end
indexTV(n,:)=[1,IndexV];
clear IndexV;
clear indexV;
 %indexTV(n,:)=[1,indexV(1,:),indexV(2,:),indexV(3,:),indexV(4,:)];
n=n+1;
 end

 
PhiWV=indexTV*Ww;
ErrorMV= 0.5*((PhiWV-ReleTest)')*(PhiWV-ReleTest);
ermsV = sqrt(2*ErrorMV/size(PhiWV,1));
ERMS=ermsV;
%clearvars n sd test testV  nm1 nm loopOfV loopOf indexT indexTV erms ermsV  Mean PhiW PhiWV ErrorM ErrorMV ;
