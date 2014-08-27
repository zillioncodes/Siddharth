function [I] = test_lr(Test,Wnew)
Tester=[ones(1500,1) Test];
 Ykk=Wnew*Tester';
 Ykk =softmax(Ykk);
Ykk=Ykk';
Label = max(Ykk,[],2);
 [Label,I] = max(Ykk,[],2);
I=I-ones(1500,1);