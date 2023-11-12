(defn v_op [f]
  (fn [a, b] (mapv f a b)))

(def v+ (v_op +))

(def v- (v_op -))

(def v* (v_op *))

(def vd (v_op /))

(defn scalar [a, b]
  (apply + (v* a b)))

(defn vect [[a_x, a_y, a_z], [b_x, b_y, b_z]]
  [(- (* a_y b_z) (* a_z b_y))
   (- (* a_z b_x) (* a_x b_z))
   (- (* a_x b_y) (* a_y b_x))])

(defn v*s [v, s]
  (mapv (partial * s) v))

(def m+ (v_op v+))

(def m- (v_op v-))

(def m* (v_op v*))

(def md (v_op vd))

(defn m*s [m, s]
  (mapv #(v*s % s) m))

(defn m*v [m, v]
  (mapv #(scalar % v) m))

(defn transpose [m]
  (apply mapv vector m))

(defn m*m [m1 m2]
  (let [m2t (transpose m2)]
    (mapv (fn [row1]
            (mapv #(scalar row1 %) m2t))
          m1)))


(defn s_op [f a b]
  (cond
    (number? a) (f a b)
    (vector? a) (mapv #(s_op f %1 %2) a b)
    ))

(def s+ (partial s_op +))
(def s- (partial s_op -))
(def s* (partial s_op *))
(def sd (partial s_op /))

