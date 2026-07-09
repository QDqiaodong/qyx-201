#!/bin/bash

set -e

echo "========================================"
echo "  研学基地科普教具管理系统 - 启动脚本"
echo "========================================"

# 检查端口占用
check_port() {
    if lsof -Pi :"$1" -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "端口 $1 已被占用!"
        lsof -Pi :"$1" -sTCP:LISTEN
        return 1
    fi
    return 0
}

echo "检查端口占用情况..."
check_port 8141 || exit 1
check_port 8151 || exit 1
check_port 3357 || exit 1
check_port 6430 || exit 1

echo "端口检查通过"
echo ""

# 启动服务
echo "启动 Docker Compose..."
docker-compose up -d --build

echo ""
echo "等待服务启动..."
sleep 60

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

echo ""
echo "========================================"
echo "  服务已启动!"
echo "========================================"
echo "  前端地址: http://localhost:8141"
echo "  后端API:  http://localhost:8151"
echo "  MySQL:    127.0.0.1:3357"
echo "  Redis:    127.0.0.1:6430"
echo "========================================"
