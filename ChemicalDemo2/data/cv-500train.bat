cd /d E:/workspace2/ChemicalDemo/data
@echo 2^-3 0
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/-3/0.model
@echo 2^-2 0
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/-2/0.model
@echo 2^-1 0
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/-1/0.model
@echo 2^0 0
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/0/0.model
@echo 2^1 0
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/1/0.model
@echo 2^2 0
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/2/0.model
@echo 2^3 0
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/0.train ./crf/cv/500/3/0.model
@echo 2^-3 1
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/-3/1.model
@echo 2^-2 1
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/-2/1.model
@echo 2^-1 1
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/-1/1.model
@echo 2^0 1
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/0/1.model
@echo 2^1 1
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/1/1.model
@echo 2^2 1
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/2/1.model
@echo 2^3 1
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/1.train ./crf/cv/500/3/1.model
@echo 2^-3 2
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/-3/2.model
@echo 2^-2 2
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/-2/2.model
@echo 2^-1 2
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/-1/2.model
@echo 2^0 2
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/0/2.model
@echo 2^1 2
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/1/2.model
@echo 2^2 2
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/2/2.model
@echo 2^3 2
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/2.train ./crf/cv/500/3/2.model
@echo 2^-3 3
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/-3/3.model
@echo 2^-2 3
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/-2/3.model
@echo 2^-1 3
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/-1/3.model
@echo 2^0 3
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/0/3.model
@echo 2^1 3
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/1/3.model
@echo 2^2 3
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/2/3.model
@echo 2^3 3
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/3.train ./crf/cv/500/3/3.model
@echo 2^-3 4
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/-3/4.model
@echo 2^-2 4
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/-2/4.model
@echo 2^-1 4
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/-1/4.model
@echo 2^0 4
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/0/4.model
@echo 2^1 4
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/1/4.model
@echo 2^2 4
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/2/4.model
@echo 2^3 4
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/4.train ./crf/cv/500/3/4.model
@echo 2^-3 5
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/-3/5.model
@echo 2^-2 5
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/-2/5.model
@echo 2^-1 5
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/-1/5.model
@echo 2^0 5
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/0/5.model
@echo 2^1 5
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/1/5.model
@echo 2^2 5
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/2/5.model
@echo 2^3 5
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/5.train ./crf/cv/500/3/5.model
@echo 2^-3 6
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/-3/6.model
@echo 2^-2 6
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/-2/6.model
@echo 2^-1 6
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/-1/6.model
@echo 2^0 6
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/0/6.model
@echo 2^1 6
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/1/6.model
@echo 2^2 6
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/2/6.model
@echo 2^3 6
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/6.train ./crf/cv/500/3/6.model
@echo 2^-3 7
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/-3/7.model
@echo 2^-2 7
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/-2/7.model
@echo 2^-1 7
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/-1/7.model
@echo 2^0 7
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/0/7.model
@echo 2^1 7
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/1/7.model
@echo 2^2 7
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/2/7.model
@echo 2^3 7
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/7.train ./crf/cv/500/3/7.model
@echo 2^-3 8
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/-3/8.model
@echo 2^-2 8
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/-2/8.model
@echo 2^-1 8
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/-1/8.model
@echo 2^0 8
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/0/8.model
@echo 2^1 8
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/1/8.model
@echo 2^2 8
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/2/8.model
@echo 2^3 8
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/8.train ./crf/cv/500/3/8.model
@echo 2^-3 9
crf_learn -f 2 -p 4 -c 0.125 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/-3/9.model
@echo 2^-2 9
crf_learn -f 2 -p 4 -c 0.25 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/-2/9.model
@echo 2^-1 9
crf_learn -f 2 -p 4 -c 0.5 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/-1/9.model
@echo 2^0 9
crf_learn -f 2 -p 4 -c 1.0 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/0/9.model
@echo 2^1 9
crf_learn -f 2 -p 4 -c 2.0 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/1/9.model
@echo 2^2 9
crf_learn -f 2 -p 4 -c 4.0 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/2/9.model
@echo 2^3 9
crf_learn -f 2 -p 4 -c 8.0 ./crf/PatentCorpus-500.template ./crf/cv/500/9.train ./crf/cv/500/3/9.model
pause
