<template>
  <canvas ref="canvas" class="network-background"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const canvas = ref(null);
let animationFrameId = null;
let mouse = { x: 0, y: 0 };

class Particle {
  constructor(x, y) {
    this.x = x;
    this.y = y;
    this.vx = (Math.random() - 0.5) * 0.5;
    this.vy = (Math.random() - 0.5) * 0.5;
    this.radius = Math.random() * 2 + 1;
  }

  update() {
    this.x += this.vx;
    this.y += this.vy;

    if (this.x < 0 || this.x > canvas.value.width) this.vx *= -1;
    if (this.y < 0 || this.y > canvas.value.height) this.vy *= -1;
  }

  draw(ctx) {
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
    ctx.fillStyle = 'rgba(255, 255, 255, 0.5)';
    ctx.fill();
  }
}

const particles = [];
const particleCount = 50;
const connectionDistance = 150;

function initCanvas() {
  const ctx = canvas.value.getContext('2d');
  canvas.value.width = window.innerWidth;
  canvas.value.height = window.innerHeight;

  // 创建粒子
  for (let i = 0; i < particleCount; i++) {
    particles.push(new Particle(
      Math.random() * canvas.value.width,
      Math.random() * canvas.value.height
    ));
  }
}

function animate() {
  const ctx = canvas.value.getContext('2d');
  ctx.clearRect(0, 0, canvas.value.width, canvas.value.height);

  // 更新和绘制粒子
  particles.forEach(particle => {
    particle.update();
    particle.draw(ctx);
  });

  // 绘制连线
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
  ctx.lineWidth = 0.5;

  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x;
      const dy = particles[i].y - particles[j].y;
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < connectionDistance) {
        ctx.beginPath();
        ctx.moveTo(particles[i].x, particles[i].y);
        ctx.lineTo(particles[j].x, particles[j].y);
        ctx.stroke();
      }
    }

    // 与鼠标的连线
    const dx = particles[i].x - mouse.x;
    const dy = particles[i].y - mouse.y;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < connectionDistance) {
      ctx.beginPath();
      ctx.moveTo(particles[i].x, particles[i].y);
      ctx.lineTo(mouse.x, mouse.y);
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.2)';
      ctx.stroke();
    }
  }

  animationFrameId = requestAnimationFrame(animate);
}

function handleMouseMove(e) {
  mouse.x = e.clientX;
  mouse.y = e.clientY;
}

function handleResize() {
  canvas.value.width = window.innerWidth;
  canvas.value.height = window.innerHeight;
}

onMounted(() => {
  initCanvas();
  animate();
  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  cancelAnimationFrame(animationFrameId);
  window.removeEventListener('mousemove', handleMouseMove);
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.network-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { 
    background-position: 0% 50%;
    background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  }
  50% { 
    background-position: 100% 50%;
    background: linear-gradient(135deg, #0f0c29, #302b63, #24243e, #3a1c71, #4a2b6a);
  }
  100% { 
    background-position: 0% 50%;
    background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  }
}
</style> 