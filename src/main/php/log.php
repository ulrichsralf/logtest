<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: *");
header("Access-Control-Allow-Headers: *");
include('logger/php/Logger.php');

Logger::configure('config.xml');
$log = Logger::getLogger('logLogger');
$json_str = file_get_contents('php://input');
$milliseconds = microtime(true) * 1000;
$method = $_SERVER['REQUEST_METHOD'];
$log->info(sprintf('%.0f', $milliseconds) . ' ' . $method . ' ' . $json_str);