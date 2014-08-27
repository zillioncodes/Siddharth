function [ErrorAct,Wnew] = train_lr(PHI)
D=size(PHI,2); % number of Dimensions
target=PHI(1:end,D);  % target labels
TestAct=PHI(1:end,1:D-1); % Test data without Bias
n=1;
N=size(PHI,1);
ErrorA(1)=0;
Inter(1)=0;
function y = softmax(y)
        rows = size(y,1);
        y = y - repmat(max(y,[],1), [rows 1]) + log(realmax)/2;
        y = exp(y);
        % normalize along columns
        y = y ./ repmat(sum(y,1), [rows 1]);    
end



while(n<N+1)
X(n)=1;
n=n+1;
end


PhiBias=[X',TestAct]; % Test data with Bias



 n=1;
 m=1;
 class(1,1)=1;
 %%% Class K (10) encoding for target
 while(n<N+1)
  while(m<11) 
     if target(n)== m-1 
         class(n,m)=1;
     else
         class(n,m)=0;
     end
     m=m+1; 
  end
 n=n+1;
 m=1;
 end
 
%----------Random Weight Generation --------------- 
 
out = rand(10,D);
W=out;
%--------------Interation for gradual decent-----------------------

itta=0.0020;
num=1;
while(num<8000)

Yk=W*PhiBias';
Yk =softmax(Yk);
Yk=Yk';
Error=Yk-class;
gradError=Error'*PhiBias;
%Yk=Yk';
m=1;
n=1;
ErrorAct=0;
while(n<N+1)
    
    
  while(m<11)
        
      ErrorAct=ErrorAct+ class(n,m)*log(Yk(n,m));
     m=m+1; 
  end
  n=n+1;
  m=1;
end
disp(num);
ErrorAct=-ErrorAct;
disp(ErrorAct);
if num>1000
Inter(num)=num;
ErrorA(num)=ErrorAct;
end
if ErrorAct==4
    break;
end
Wnew=W-(itta*gradError);
W=Wnew;
num=num+1;
end

plot(Inter,ErrorA);

end
%-----------------------------------------------------------------------
