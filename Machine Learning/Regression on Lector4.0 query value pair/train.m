


m=8;

lambda=2;

warning('off','all');

loopOf=size(matLearing,1);
n=1;
nm=1;
nm1=1;
while(n<loopOf+1)
     nm=1;
 while(nm<m+1)
   
     if nm==1
             test=matLearing(1:(size(matLearing,1)/m),1:end);
         Mean(nm,:)=mean(test);
     sd(nm)=std2(test);
     else 
         test=matLearing((size(matLearing,1)*((nm-1)/m)):(size(matLearing,1)*(nm/m)),1:end);

         Mean(nm,:)=mean(test);
         sd(nm)=std2(test);

     end
     nm=nm+1;
 end
 
 nm1=1;
 
 while(nm1<m+1)
index(nm1,:)=exp(-(((matLearing(n,:)-Mean(nm1,:)).^2)/(2*(sd(nm1)^2))));
nm1=nm1+1;
 end
 
nm1=1;

while(nm1<m+1)
    if nm1==1
    Index=index(nm1,:);
    else
        test=Index;
        Index=[test,index(nm1,:)];
    end;     
    nm1=nm1+1;
end
indexT(n,:)=[1,Index];
clear Index;
clear index;
n=n+1;
end






Ww=(pinv(((indexT')*indexT)+lambda*eye(size(indexT,2),size(indexT,2))))*((indexT')*(ReleMat));
PhiW=indexT*Ww;
ErrorM= 0.5*((PhiW-ReleMat)')*(PhiW-ReleMat);
erms = sqrt(2*ErrorM/size(PhiW,1));
ermsValid=predict(matValid,ReleValid,Mean,sd,Ww,8,2);
ermsTest=predict(matTest,ReleTest,Mean,sd,Ww,8,2);
rms_lr=ermsTest;
%validation           

% 
%  loopOfV=size(matValid,1);
% % i=1;
% 
%  n=1;
%  nm=1;
%  nm1=1;
%  while(n<loopOfV+1)
%  nm1=1;
%  while(nm1<m+1)
% indexV(nm1,:)=exp(-(((matValid(n,:)-Mean(nm1,:)).^2)/(2*(sd(nm1)^2))));
% nm1=nm1+1;
%  end
%  nm1=1;
%  while(nm1<m+1)
%     if nm1==1
%     IndexV=indexV(nm1,:);
%     else
%         testV=IndexV;
%         IndexV=[testV,indexV(nm1,:)];
%     end;     
%     nm1=nm1+1;
% end
% indexTV(n,:)=[1,IndexV];
% clear IndexV;
% clear indexV;
%  %indexTV(n,:)=[1,indexV(1,:),indexV(2,:),indexV(3,:),indexV(4,:)];
% n=n+1;
%  end
% 
%  
% PhiWV=indexTV*Ww;
% ErrorMV= 0.5*((PhiWV-ReleValid)')*(PhiWV-ReleValid);
% ermsV = sqrt(2*ErrorMV/size(PhiWV,1))

M=m;
[net tr] = nn_model(matTestSup', matSuperValid');
rms_nn = min(tr.tperf);
rms_nn = sqrt(rms_nn);

fprintf('the model complexity M for the linear regression model is %d\n', M);
fprintf('the regularization parameters lambda for the linear regression model is %f\n', lambda);
fprintf('the root mean square error for the linear regression model is %f\n', rms_lr);
fprintf('the root mean square error for the neural network model is %f\n', rms_nn);
clearvars n  test testV  nm1 nm loopOfV loopOf indexT indexTV PhiW PhiWV ErrorM ErrorMV ;
