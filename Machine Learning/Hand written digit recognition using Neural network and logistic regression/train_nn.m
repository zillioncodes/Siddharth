function [omegaHid, omegaOut, M] = train_nn2(PHI)
% Neural network Classifier returns the values of w. 


% l variables
A(1)=1;
B(1)=1;
N = size(PHI, 1); 
D = size(PHI, 2); % how many parameters (features)
itta = 0.00025;
M = 30;
m=1;
T = PHI(:, D);
PHI = PHI(:, 1:D-1);
K = unique(T);
k = size(K,1);
X = [ones(N, 1) PHI]; % x0 term as 1 in feature matrix
WHid = 0.5 - rand(M, D); % Hidden layer weights(D =513)
WOut = 0.5 - rand(size(K,1), M + 1); % Output layer weights


 %%% Class K (10) encoding for target
vSize = size(T);
t = sparse(vSize(1),max(T)+1);
for i = 1:vSize(1)
    t(i, T(i) +1) = 1;
end
t = full(t);
 
format long;
[omegaHid, omegaOut] = minfun(X, WHid, WOut,t);

    
    function [WHidnew, WOutnew] = minfun(phi, WHidold, WOutold, target)
        WHidnew = WHidold;
        WOutnew = WOutold;

        for iter = 1:4000 %  number of iterations
            [zj, y] = feed_forward(phi, WHidnew, WOutnew);
            WHidold = WHidnew;
            WOutold = WOutnew;
            [WHidnew, WOutnew] = backprop(phi,y, target, WHidold, WOutold, zj);
         %   computeErr(phi,y',target);
            
            
            
        nllError = 0;
        ymat=y';
        for ni = 1:length(phi)
            for kj = 1:10
                
                nllError = nllError + target(ni,kj)*log(ymat(ni,kj));
            end
        end
        nllError = -1*nllError; 
        B(iter)=nllError;
        A(iter)=iter;
        disp(nllError);
        if(nllError < 2500)
            itta = 0.000275;
        elseif(nllError < 1000)
            itta = 0.000265;
        end
        
             
    
             
             
             
             
             
             
             
        end
    end
    
   
    function [hidden_act, output_act] = feed_forward(phi, Whid, WOut)
        temp = Whid*phi';

        hidden_act = tanh([ones(1, N); temp]);
        
%         hidden_act = hidden_act';
        output_act = softmax(WOut*hidden_act);
    end

    % Error Back propagation
    function [W_H, W_O] =  backprop(xi, yk, tk, wHid, wOut, z_j)
        delk = yk - tk';
        der_tanh = 1 - (z_j(2:size(z_j,1),:).*z_j(2:size(z_j,1),:));

        tt2 = wOut'*delk;
        delj = der_tanh.*(tt2(2:size(tt2,1),:));
        gradHid = delj*xi;
        gradOut = delk*z_j';
        W_H = wHid - itta*gradHid;
        W_O = wOut - itta*gradOut;
    end

    function y = softmax(y)
        rows = size(y,1);
        y = y - repmat(max(y,[],1), [rows 1]) + log(realmax)/2;
        y = exp(y);
        % normalize along columns
        y = y ./ repmat(sum(y,1), [rows 1]);    
    end
    
   
    
   

%plot(B,A);
end
