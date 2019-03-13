(define (fibonacci n)
  {let fib ([prev 0] ;comment define let secrets
            [cur 1]
            [i 0]}
     (if (= i n)
        #\t
        (fib cur (+ prev cur) (+ i 1)))))
