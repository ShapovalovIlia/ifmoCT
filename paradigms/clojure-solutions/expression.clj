(defn constant [val]
  (fn [_]
    val))

(defn variable [var]
  (fn [vars]
    (vars var)))

(defn unary_op [f]
  (fn [val]
    (fn [vars]
      (f (val vars)))))

(defn binary_op [f]
  (fn [a b]
    (fn [vars]
      (f (a vars) (b vars)))))

(def negate (unary_op -))

(def arcTan (unary_op
              (fn [val]
                (Math/atan val))))

(def add (binary_op +))

(def subtract (binary_op -))

(def multiply (binary_op *))

(def correct_division
  (fn [a, b]
    (/ a (double b))))

(def divide (binary_op correct_division))

(def arcTan2 (binary_op
               (fn [a, b]
                 (Math/atan2 a b))))

(def ops {'negate negate,
          '+      add,
          '-      subtract,
          '*      multiply,
          '/      divide,
          'atan   arcTan,
          'atan2  arcTan2
          }
  )

(defn parse [expr]
  (cond
    (number? expr) (constant expr)
    (symbol? expr) (variable (str expr))
    (seq? expr) (apply (get ops (first expr)) (map parse (rest expr)))))

(defn parseFunction [str]
  (parse (read-string str)))

; 11 hw

(defn evaluate [expression vars]
  ((:evaluate expression) vars))

(defn toString [expression]
  (:toString expression))

(defn Constant [val]
  {
   :evaluate (fn [_] val)
   :toString (str val)
   })

(defn Variable [var]
  {
   :evaluate (fn [vars] (vars var))
   :toString (str var)
   })

(defn obj_unary_op [f, sign]
  (fn [val]
    {
     :evaluate (fn [vars] (f ((:evaluate val) vars)))
     :toString (str "(" sign " " (:toString val) ")")
     }))


(defn obj_binary_op [f sign]
  (fn [a, b]
    {
     :evaluate (fn [vars] (f ((:evaluate a) vars) ((:evaluate b) vars)))
     :toString (str "(" sign " " (:toString a) " " (:toString b) ")")
     }))

(def Add (obj_binary_op + "+"))

(def Subtract (obj_binary_op - "-"))

(def Multiply (obj_binary_op * "*"))

(def Divide (obj_binary_op correct_division "/"))

(def Negate (obj_unary_op - "negate"))

(def Sinh (obj_unary_op
            (fn [val]
              (Math/sinh val)) "sinh"))
(def Cosh (obj_unary_op
            (fn [val]
              (Math/cosh val)) "cosh"))


(def obj_ops {'+      Add,
              '-      Subtract,
              '*      Multiply,
              '/      Divide,
              'negate Negate,
              'sinh   Sinh,
              'cosh   Cosh
              }
  )

(defn obj_parse [expr]
  (cond
    (number? expr) (Constant expr)
    (symbol? expr) (Variable (str expr))
    (seq? expr) (apply (get obj_ops (first expr)) (map obj_parse (rest expr)))))

(defn parseObject [str]
  (obj_parse (read-string str)))


