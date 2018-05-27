<?php
/**
 * Created by PhpStorm.
 * User: pierre
 * Date: 27/05/2018
 * Time: 17:46
 */
require 'vendor/autoload.php';
use GuzzleHttp\Client;


$client = new Client([
    // Base URI is used with relative requests
    'base_uri' => 'https://calm-cliffs-46267.herokuapp.com',
    // You can set any number of default request options.
    'timeout'  => 2.0,
]);


