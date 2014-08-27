function prediction = predict_nn(omega1, omega2, Y )
m = size(Y, 1);
prediction = zeros(size(Y, 1), 1);

% Add ones to the X data matrix to account for x0
Y = [ones(m, 1) Y];
aj = tanh(omega1*Y');
tempProb = [tanh(ones(size(Y,1),1)) aj'];
tempProb = softmax(tempProb*omega2');
[output,prediction] = max(tempProb,[],2);
prediction = prediction -1;

    function y = softmax(y)
        rows = size(y,1);
        y = y - repmat(max(y,[],1), [rows 1]) + log(realmax)/2;
        y = exp(y);
        % normalize along columns
        y = y ./ repmat(sum(y,1), [rows 1]);    
    end
end

