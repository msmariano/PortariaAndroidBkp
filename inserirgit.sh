#!/bin/bash
echo 'Informe o nome do projeto ou arquivo para fazer o commit'
read projeto
echo 'Informe a ação: 1 - criar 2 - commit'
read acao
if [ "$projeto" != "" ] ;
then
if [ "$acao" = "1" ]
then
git init
git add .
git commit -m 'initial commit'
git remote add origin https://github.com/msmariano/$projeto.git
git push -u origin master
echo 'Sucesso!'
else
if [ "$acao" = "2" ]
then
git add $projeto
git commit
git push -u origin master
fi
fi
fi

