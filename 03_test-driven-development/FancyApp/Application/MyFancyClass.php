<?php
/**
 * Created by PhpStorm.
 * User: s.kemper
 * Date: 29.03.2022
 * Time: 22:05
 *
 * This class does a lot of things... but has no productive purpose.
 * The only purpose of this class is to teach the basic functions
 * of PHP and how to use them.
 */

class MyFancyClass
{
    public function shortString($text, $length, $ending = '...') {
        // the ending has to fit into the requested length
        if (mb_strlen($ending) > $length) {
            return false;
        }
        if (mb_strlen($text) <= $length) {
            return $text;
        }
        return mb_substr($text, 0, $length - mb_strlen($ending)) . $ending;
    }

    public function calcAverage($values) {
        if (count($values) === 0) {
            return 0;
        }
        $sum = 0;
        foreach ($values as $value) {
            // numeric strings like '4.5' are allowed, words like 'bestanden' are not
            if (!is_numeric($value)) {
                return false;
            }
            $sum += (float) $value;
        }
        return $sum / count($values);
    }

    public function getOpposite($value, $delimiter = ',') {
    }
}