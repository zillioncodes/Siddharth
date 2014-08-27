require('ggplot2')
View('20aprWords');

apr20Words=subset(`20aprWords`,`20aprWords`$Count>100)
apr20Words=apr20Words[1:50,]

apr20Hash=apr20Hash[order(-apr20Hash$X513),]
apr20Hash=apr20Hash[1:50,]

apr20Add=apr20Add[order(-apr20Add$V2),]
apr20Add=apr20Add[1:50,]

apr21Words=apr21Words[order(-apr21Words$V2),]
apr21Words=apr21Words[1:50,]

apr21Hash=apr21Hash[order(-apr21Hash$X183),]
apr21Hash=apr21Hash[1:50,]

apr21Add=apr21Add[order(-apr21Add$V2),]
apr21Add=apr21Add[1:50,]



apr22Words=apr22Words[order(-apr22Words$V2),]
apr22Words=apr22Words[1:50,]

apr22Hash=apr22Hash[order(-apr22Hash$X393),]
apr22Hash=apr22Hash[1:50,]

apr22Add=apr22Add[order(-apr22Add$V2),]
apr22Add=apr22Add[1:50,]

qplot(apr20Words$Word,apr20Words$Count,geom="histogram",fill=-apr20Words$Count,xlab="Words",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr20Words$Count), position="dodge")

qplot(apr20Hash$X.,apr20Hash$X513,geom="histogram",fill=-apr20Hash$X513,xlab="HashTag",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr20Hash$X513), position="dodge")

qplot(apr20Add$V1,apr20Add$V2,geom="histogram",fill=-apr20Add$V2,xlab="Addtag(@)",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr20Add$V2), position="dodge")
wordcloud(apr20Hash$X., apr20Hash$X513, random.order=FALSE, colors=brewer.pal(8, "Dark2"))




qplot(apr21Words$V1,apr21Words$V2,geom="histogram",fill=-apr21Words$V2,xlab="Words",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr21Words$V2), position="dodge")

qplot(apr21Hash$X.,apr21Hash$X183,geom="histogram",fill=-apr21Hash$X183,xlab="HashTag",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr21Hash$X183), position="dodge")
wordcloud(apr21Hash$X., apr21Hash$X183, random.order=FALSE, colors=brewer.pal(8, "Dark2"))


qplot(apr21Add$V1,apr21Add$V2,geom="histogram",fill=-apr21Add$V2,xlab="Addtag(@)",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr21Add$V2), position="dodge")



qplot(apr22Words$V1,apr22Words$V2,geom="histogram",fill=-apr22Words$V2,xlab="Words",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr22Words$V2), position="dodge")

qplot(apr22Hash$X.,apr22Hash$X393,geom="histogram",fill=-apr22Hash$X393,xlab="HashTag",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr22Hash$X393), position="dodge")
wordcloud(apr22Hash$X., apr22Hash$X393, random.order=FALSE, colors=brewer.pal(8, "Dark2"))

qplot(apr22Add$V1,apr22Add$V2,geom="histogram",fill=-apr22Add$V2,xlab="Addtag(@)",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=apr22Add$V2), position="dodge")


PoliWord=PoliWord[order(-PoliWord$V2),]
//PoliWord=subset(PoliWord,PoliWord$V1!="on"|PoliWord$V1!="of"|PoliWord$V1!="the"|PoliWord$V1!="am"|PoliWord$V1!="on"|PoliWord$V1!="about"|PoliWord$V1!="and"|PoliWord$V1!="can't"|PoliWord$V1!="down"|PoliWord$V1!="each")
PoliWord=PoliWord[1:50,]
qplot(PoliWord$V1,PoliWord$V2,geom="histogram",fill=-apr22Words$V2,xlab="Words",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=PoliWord$V2), position="dodge")


PoliHash=PoliHash[order(-PoliHash$X69),]
PoliHash=PoliHash[1:50,]
qplot(PoliHash$X.,PoliHash$X69,geom="histogram",fill=-PoliHash$X69,xlab="HashTag",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=PoliHash$X69), position="dodge")
wordcloud(PoliHash$X., PoliHash$X69, random.order=FALSE, colors=brewer.pal(8, "Dark2"))


PoliAdd=PoliAdd[order(-PoliAdd$V2),]
PoliAdd=PoliAdd[1:50,]
qplot(PoliAdd$V1,PoliAdd$V2,geom="histogram",fill=-PoliAdd$V2,xlab="AddTag(@)",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=PoliAdd$V2), position="dodge")

plot(log2(intrK1$V1), log2(intrK1$V1), col=intrK1$V2)
plot(log2(intrK2$V1), log2(intrK2$V1), col=intrK2$V2)
plot(log2(intrK3$V1), log2(intrK3$V1), col=intrK3$V2)
plot(log2(intrK4$V1), log2(intrK4$V1), col=intrK4$V2)
plot(log2(intrK5$V1), log2(intrK5$V1), col=intrK5$V2)
plot(log2(intrK6$V1), log2(intrK6$V1), col=intrK6$V2)
plot(log2(intrK7$V1), log2(intrK7$V1), col=intrK7$V2)
plot(log2(intrK8$V1), log2(intrK8$V1), col=intrK8$V2)



 intrK8 <- read.table("C:/Users/SIDDHARTH/Desktop/kmean/kmeans/input8.txt", quote="\"")



qplot(log10(intrK1$V1),intrK1$V3,geom="point",fill=-intrK1$V3,xlab="Addtag(@)",ylab="Count")+geom_point(aes(weights=-intrK1$V2), position="dodge")




stripecount=stripecount[order(-stripecount$count),]
stripecount=stripecount[1:50,]

qplot(stripecount$hashtag,stripecount$count,geom="histogram",fill=-stripecount$count,xlab="Co-occuring HashTag",ylab="Count")+opts(axis.text.x=theme_text(angle=-90,size=12))+geom_bar(aes(weights=stripecount$count), position="dodge")

uni=unique(RelativeFr$RF)

clus1=subset(RelativeFr,RelativeFr$RF<0.25)
clus2=subset(RelativeFr,RelativeFr$RF>=0.25|RelativeFr$RF<0.5)
clus3=subset(RelativeFr,RelativeFr$RF>=0.5|RelativeFr$RF<0.75)
clus4=subset(RelativeFr,RelativeFr$RF>=0.75)
value<-c(nrow(clus1),nrow(clus2),nrow(clus3),nrow(clus4))
lbs=c("RF:0-0.25","RF:0.25-0.50","RF:0.5-0.75","RF:0.75-1")
require('plotrix')
pie3D(value,labels=lbs,explode=0.2,main="Relative frequency distribution")








  
