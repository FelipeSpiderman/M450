<?php
/**
 * Simulierte zweite Gruppe für Aufgabe 3 (Vergleich).
 * Gleiche Tests, anderer Lösungsweg.
 */

class MyFancyClass
{
    public function shortString($text, $length, $ending = '...') {
        if (mb_strlen($ending) > $length) {
            return false;
        }
        return mb_strimwidth($text, 0, $length, $ending);
    }

    public function calcAverage($values) {
        if (empty($values)) {
            return 0;
        }
        foreach ($values as $value) {
            if (!is_numeric($value)) {
                return false;
            }
        }
        return array_sum($values) / count($values);
    }

    public function getOpposite($value, $delimiter = ',') {
        if (is_array($value)) {
            return implode($delimiter, $value);
        }
        if (is_string($value)) {
            return explode($delimiter, $value);
        }
        return false;
    }
}
