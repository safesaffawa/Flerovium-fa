package com.safe.flerovium.functions;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MatrixStuff {
   public MatrixStuff() {
   }

   public static void rotateXY(Matrix4f dest, float sinX, float cosX, float sinY, float cosY) {
      float lm00 = dest.m00();
      float lm01 = dest.m01();
      float lm02 = dest.m02();
      float lm03 = dest.m03();
      float lm10 = dest.m10();
      float lm11 = dest.m11();
      float lm12 = dest.m12();
      float lm13 = dest.m13();
      float lm20 = dest.m20();
      float lm21 = dest.m21();
      float lm22 = dest.m22();
      float lm23 = dest.m23();
      float m_sinX = -sinX;
      float m_sinY = -sinY;
      float xm20 = Math.fma(lm10, m_sinX, lm20 * cosX);
      float xm21 = Math.fma(lm11, m_sinX, lm21 * cosX);
      float xm22 = Math.fma(lm12, m_sinX, lm22 * cosX);
      float xm23 = Math.fma(lm13, m_sinX, lm23 * cosX);
      float xm10 = Math.fma(lm10, cosX, lm20 * sinX);
      float xm11 = Math.fma(lm11, cosX, lm21 * sinX);
      float xm12 = Math.fma(lm12, cosX, lm22 * sinX);
      float xm13 = Math.fma(lm13, cosX, lm23 * sinX);
      float nm00 = Math.fma(lm00, cosY, xm20 * m_sinY);
      float nm01 = Math.fma(lm01, cosY, xm21 * m_sinY);
      float nm02 = Math.fma(lm02, cosY, xm22 * m_sinY);
      float nm03 = Math.fma(lm03, cosY, xm23 * m_sinY);
      float ym20 = Math.fma(lm00, sinY, xm20 * cosY);
      float ym21 = Math.fma(lm01, sinY, xm21 * cosY);
      float ym22 = Math.fma(lm02, sinY, xm22 * cosY);
      float ym23 = Math.fma(lm03, sinY, xm23 * cosY);
      dest.set(nm00, nm01, nm02, nm03, xm10, xm11, xm12, xm13, ym20, ym21, ym22, ym23, dest.m30(), dest.m31(), dest.m32(), dest.m33());
   }

   public static void rotateXY(Matrix3f dest, float sinX, float cosX, float sinY, float cosY) {
      float m_sinX = -sinX;
      float m_sinY = -sinY;
      float nm10 = Math.fma(dest.m10, cosX, dest.m20 * sinX);
      float nm11 = Math.fma(dest.m11, cosX, dest.m21 * sinX);
      float nm12 = Math.fma(dest.m12, cosX, dest.m22 * sinX);
      float nm20 = Math.fma(dest.m10, m_sinX, dest.m20 * cosX);
      float nm21 = Math.fma(dest.m11, m_sinX, dest.m21 * cosX);
      float nm22 = Math.fma(dest.m12, m_sinX, dest.m22 * cosX);
      float nm00 = Math.fma(dest.m00, cosY, nm20 * m_sinY);
      float nm01 = Math.fma(dest.m01, cosY, nm21 * m_sinY);
      float nm02 = Math.fma(dest.m02, cosY, nm22 * m_sinY);
      dest.m20 = Math.fma(dest.m00, sinY, nm20 * cosY);
      dest.m21 = Math.fma(dest.m01, sinY, nm21 * cosY);
      dest.m22 = Math.fma(dest.m02, sinY, nm22 * cosY);
      dest.m00 = nm00;
      dest.m01 = nm01;
      dest.m02 = nm02;
      dest.m10 = nm10;
      dest.m11 = nm11;
      dest.m12 = nm12;
   }
}
